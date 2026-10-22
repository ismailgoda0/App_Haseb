package com.homecash.core.simulation

import com.homecash.data.local.AppDatabase
import com.homecash.data.local.entity.FlagEntity
import com.homecash.data.local.entity.TransactionEntity
import com.homecash.domain.parser.SmsParserEngine
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class SimulationEvent(
    val deviceName: String,
    val walletProvider: String,
    val direction: String,
    val amount: Double,
    val summary: String,
    val timestamp: Long = System.currentTimeMillis()
)

object MockSmsSimulationService {

    private var simulationJob: Job? = null
    private val _isSimulationRunning = MutableStateFlow(false)
    val isSimulationRunning = _isSimulationRunning.asStateFlow()

    private val _simulationEvents = MutableSharedFlow<SimulationEvent>(extraBufferCapacity = 10)
    val simulationEvents = _simulationEvents.asSharedFlow()

    /**
     * Starts background periodic ticker (runs every 30 seconds)
     */
    fun startSimulation(db: AppDatabase, scope: CoroutineScope) {
        if (_isSimulationRunning.value) return
        _isSimulationRunning.value = true

        simulationJob = scope.launch(Dispatchers.IO) {
            while (isActive && _isSimulationRunning.value) {
                delay(30000L) // 30 seconds interval
                simulateOneTransaction(db)
            }
        }
    }

    /**
     * Stops the simulation
     */
    fun stopSimulation() {
        _isSimulationRunning.value = false
        simulationJob?.cancel()
        simulationJob = null
    }

    /**
     * Instant trigger from UI button ("محاكاة وصول رسالة الآن")
     */
    suspend fun triggerManualSimulation(db: AppDatabase) = withContext(Dispatchers.IO) {
        simulateOneTransaction(db)
    }

    private suspend fun simulateOneTransaction(db: AppDatabase) {
        try {
            val wallets = db.walletDao().getWalletsSync("admin")
            if (wallets.isEmpty()) return

            val targetWallet = wallets.random()
            val scenario = MockEgyptianSmsTemplates.generateRandomScenario(targetWallet.currentBalance)

            // Parse via the real production SmsParserEngine
            val parsed = SmsParserEngine.parse(scenario.sender, scenario.text)

            // Calculate cashier commission for business mode
            val commission = when {
                parsed.direction == "IN" -> (parsed.amount * (targetWallet.receiveFeePct / 100.0)).coerceAtLeast(0.0)
                else -> (parsed.amount * (targetWallet.sendFeePct / 100.0)).coerceAtLeast(3.0)
            }

            // Calculate new wallet balance
            val balanceChange = if (parsed.direction == "IN") parsed.amount else -(parsed.amount + parsed.fee)
            val computedNewBalance = (targetWallet.currentBalance + balanceChange).coerceAtLeast(0.0)
            val finalBalance = parsed.balance ?: computedNewBalance

            // Check reconciliation flag
            if (parsed.balance != null && Math.abs(parsed.balance - computedNewBalance) > 0.01) {
                val flag = FlagEntity(
                    id = UUID.randomUUID().toString(),
                    userId = "admin",
                    walletId = targetWallet.id,
                    type = "BALANCE_MISMATCH",
                    status = "OPEN",
                    title = "فرق في رصيد ${targetWallet.providerName}",
                    explanation = "وصلت رسالة برصيد ${parsed.balance} ج.م بينما الرصيد المحسوب هو ${computedNewBalance} ج.م.",
                    expectedBalance = computedNewBalance,
                    actualBalance = parsed.balance,
                    discrepancy = Math.abs(parsed.balance - computedNewBalance)
                )
                db.flagDao().insertFlag(flag)
            }

            // Find matching device ID
            val devices = db.deviceDao().getDevicesSync("admin")
            val matchedDevice = devices.firstOrNull { it.deviceName == scenario.deviceName } ?: devices.firstOrNull()
            val deviceId = matchedDevice?.id ?: "dev-cashier-1"

            // Insert transaction
            val tx = TransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = "admin",
                walletId = targetWallet.id,
                deviceId = deviceId,
                fingerprint = "${parsed.operationCode}-${parsed.amount}-${System.currentTimeMillis()}",
                direction = parsed.direction,
                type = if (parsed.direction == "IN") "TRANSFER" else "TRANSFER",
                amount = parsed.amount,
                fee = parsed.fee,
                cashierCommission = commission,
                balanceAfter = finalBalance,
                counterpartyNumber = parsed.counterpartyNumber,
                counterpartyName = parsed.counterpartyName,
                operationCode = parsed.operationCode,
                occurredAt = System.currentTimeMillis(),
                status = "CONFIRMED",
                category = parsed.category,
                note = "وارد من محاكاة [${scenario.deviceName}]",
                rawText = scenario.text
            )
            db.transactionDao().insertTransaction(tx)

            // Update wallet balance
            db.walletDao().updateBalance("admin", targetWallet.id, finalBalance)

            // Update device sync timestamp
            matchedDevice?.let {
                db.deviceDao().updateDeviceSyncTime(it.id)
            }

            // Emit live UI event
            val summary = if (parsed.direction == "IN") {
                "استلام ${"%.2f".format(parsed.amount)} ج.م على ${targetWallet.providerName}"
            } else {
                "إرسال ${"%.2f".format(parsed.amount)} ج.م من ${targetWallet.providerName}"
            }

            // Post native Android system notification to lock screen & status bar
            com.homecash.core.notifications.AppNotificationCenter.post(
                title = if (parsed.direction == "IN") "وارد كاش: +${"%.2f".format(parsed.amount)} ج.م ⚡" else "صادر كاش: -${"%.2f".format(parsed.amount)} ج.م",
                message = "${targetWallet.providerName} • ${if (parsed.counterpartyNumber.isNotBlank()) parsed.counterpartyNumber else scenario.deviceName} • الرصيد: ${"%.2f".format(finalBalance)} ج.م",
                category = "عملية محفظة"
            )

            _simulationEvents.emit(
                SimulationEvent(
                    deviceName = scenario.deviceName,
                    walletProvider = targetWallet.providerName,
                    direction = parsed.direction,
                    amount = parsed.amount,
                    summary = summary
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
