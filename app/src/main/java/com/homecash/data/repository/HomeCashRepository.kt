package com.homecash.data.repository

import com.homecash.data.local.AppDatabase
import com.homecash.data.local.entity.*
import com.homecash.data.remote.CloudSyncApi
import com.homecash.core.notifications.AppNotificationCenter
import com.homecash.domain.model.CounterpartySummary
import com.homecash.domain.parser.ParsedSmsResult
import com.homecash.domain.parser.SmsParserEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import android.content.Context
import java.util.UUID

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class HomeCashRepository(
    private val db: AppDatabase,
    private val context: Context? = null
) {

    private val sessionPrefs = context?.getSharedPreferences("homecash_session", Context.MODE_PRIVATE)
    private val activeUser = MutableStateFlow(sessionPrefs?.getString("active_user_id", "") ?: "")

    fun setActiveUser(userId: String) {
        activeUser.value = userId
        sessionPrefs?.edit()?.putString("active_user_id", userId)?.apply()
    }
    fun activeUserId(): String = activeUser.value

    // User & Settings
    val currentUser: Flow<UserEntity?> = activeUser.flatMapLatest { db.userDao().getUser(it) }

    suspend fun setMode(mode: String) = withContext(Dispatchers.IO) {
        db.userDao().updateMode(activeUser.value, mode)
    }

    suspend fun updateUserProfile(user: UserEntity) = withContext(Dispatchers.IO) {
        if (activeUser.value.isNotBlank() && user.id == activeUser.value) db.userDao().updateUser(user)
    }

    suspend fun completeInitialSetup(
        user: UserEntity,
        device: DeviceEntity,
        line: LineEntity,
        wallet: WalletEntity
    ) = withContext(Dispatchers.IO) {
        db.userDao().insertUser(user)
        db.deviceDao().insertDevice(device)
        db.lineDao().insertLine(line)
        db.walletDao().insertWallet(wallet)
        CloudSyncApi.syncWalletBalance(user.id, wallet)
        CloudSyncApi.syncDeviceHeartbeat(user.id, device)
    }

    // Wallets & Lines
    val wallets: Flow<List<WalletEntity>> = activeUser.flatMapLatest { db.walletDao().getWallets(it) }
    val lines: Flow<List<LineEntity>> = activeUser.flatMapLatest { db.lineDao().getLines(it) }
    val devices: Flow<List<DeviceEntity>> = activeUser.flatMapLatest { db.deviceDao().getDevices(it) }

    fun getWalletById(id: String): Flow<WalletEntity?> = activeUser.flatMapLatest { db.walletDao().getWalletById(it, id) }

    suspend fun addWallet(wallet: WalletEntity) = withContext(Dispatchers.IO) {
        val owner = activeUser.value
        if (owner.isBlank() || wallet.userId != owner) return@withContext
        db.walletDao().insertWallet(wallet)
        CloudSyncApi.syncWalletBalance(owner, wallet)
    }

    suspend fun updateWallet(wallet: WalletEntity) = withContext(Dispatchers.IO) {
        val owner = activeUser.value
        if (owner.isBlank() || wallet.userId != owner) return@withContext
        // Confirm that the row already belongs to this user before updating by primary key.
        if (db.walletDao().getWalletByIdSync(owner, wallet.id) == null) return@withContext
        db.walletDao().updateWallet(wallet)
        CloudSyncApi.syncWalletBalance(owner, wallet)
    }

    // Transactions
    val allTransactions: Flow<List<TransactionEntity>> = activeUser.flatMapLatest { db.transactionDao().getAllTransactions(it) }
    val recentTransactions: Flow<List<TransactionEntity>> = activeUser.flatMapLatest { db.transactionDao().getRecentTransactions(it, 15) }

    fun getTransactionsByWallet(walletId: String): Flow<List<TransactionEntity>> =
        activeUser.flatMapLatest { db.transactionDao().getTransactionsByWallet(it, walletId) }

    fun searchTransactions(query: String): Flow<List<TransactionEntity>> =
        activeUser.flatMapLatest { db.transactionDao().searchTransactions(it, query) }

    suspend fun getTransactionById(id: String): TransactionEntity? = withContext(Dispatchers.IO) {
        db.transactionDao().getTransactionById(activeUser.value, id)
    }

    // Flags & Reconciliation
    val openFlags: Flow<List<FlagEntity>> = activeUser.flatMapLatest { db.flagDao().getOpenFlags(it) }
    val openFlagsCount: Flow<Int> = activeUser.flatMapLatest { db.flagDao().getOpenFlagsCount(it) }

    suspend fun resolveFlag(flagId: String, action: String) = withContext(Dispatchers.IO) {
        db.flagDao().resolveFlag(activeUser.value, flagId, action)
        AppNotificationCenter.post("تمت معالجة التنبيه", "تم حفظ إجراء المراجعة وتحديث سجل التدقيق.", "عملية")
    }

    suspend fun batchResolveFlags(type: String, action: String) = withContext(Dispatchers.IO) {
        db.flagDao().batchResolveFlags(activeUser.value, type, action)
        AppNotificationCenter.post("اكتملت المراجعة الجماعية", "تم تنفيذ إجراء المراجعة الجماعية وحفظه في السجل.", "عملية")
    }

    // Counterparty Summary
    suspend fun getCounterpartySummary(counterparty: String): CounterpartySummary = withContext(Dispatchers.IO) {
        val txs = db.transactionDao().getTransactionsByCounterpartySync(activeUser.value, counterparty)
        var totalSent = 0.0
        var totalReceived = 0.0
        var totalCommissions = 0.0
        var name = counterparty

        for (tx in txs) {
            if (tx.counterpartyName.isNotBlank()) {
                name = tx.counterpartyName
            }
            if (tx.direction == "IN") {
                totalReceived += tx.amount
            } else {
                totalSent += tx.amount
            }
            totalCommissions += tx.cashierCommission
        }

        CounterpartySummary(
            counterpartyNumber = counterparty,
            counterpartyName = name,
            totalSent = totalSent,
            totalReceived = totalReceived,
            netBalance = totalReceived - totalSent,
            totalCommissions = totalCommissions,
            transactionCount = txs.size,
            transactions = txs
        )
    }

    /**
     * Ingests real SMS received by SmsReceiver
     */
    suspend fun processIncomingSms(sender: String, messageBody: String): Boolean = withContext(Dispatchers.IO) {
        // Never attach a real SMS to the demo account or another user's ledger before authentication.
        if (activeUser.value.isBlank()) return@withContext false
        val parsed = SmsParserEngine.parse(sender, messageBody)

        // 1. Anti-fraud check
        if (parsed.isPotentialFraud) {
            val fraudFlag = FlagEntity(
                id = UUID.randomUUID().toString(),
                userId = activeUser.value,
                walletId = "unknown",
                type = "UNKNOWN_SMS",
                status = "OPEN",
                title = "⚠️ تحذير احتيال محتمل",
                explanation = parsed.fraudReason ?: "رسالة مشبوهة غير معتمدة تدعي تحويل أموال.",
                actualBalance = parsed.amount
            )
            db.flagDao().insertFlag(fraudFlag)
            AppNotificationCenter.post("رسالة تحتاج إلى مراجعة", "تم إيقاف رسالة مشبوهة عن تعديل رصيد المحفظة. راجع التنبيهات داخل التطبيق.", "مراجعة")
            return@withContext false
        }

        // 2. Match wallet
        val wallets = db.walletDao().getWalletsSync(activeUser.value)
        val targetWallet = wallets.firstOrNull { it.providerName.contains(parsed.walletProvider, true) }
            ?: wallets.firstOrNull()

        if (targetWallet == null) return@withContext false

        // 3. Deduplication check
        val fingerprint = "${parsed.operationCode}-${parsed.amount}-${parsed.counterpartyNumber}"
        if (db.transactionDao().getTransactionByFingerprint(activeUser.value, fingerprint) != null) {
            val dupFlag = FlagEntity(
                id = UUID.randomUUID().toString(),
                userId = activeUser.value,
                walletId = targetWallet.id,
                type = "DUPLICATE",
                status = "OPEN",
                title = "تكرار محتمل للعملية",
                explanation = "وصلت رسالة بنفس كود العملية (${parsed.operationCode}) ومبلغ ${parsed.amount} ج.م",
                expectedBalance = targetWallet.currentBalance,
                actualBalance = targetWallet.currentBalance
            )
            db.flagDao().insertFlag(dupFlag)
            AppNotificationCenter.post("عملية مكررة محتملة", "وصلت رسالة تشبه عملية مسجلة؛ لم يتم تحديث الرصيد تلقائياً.", "مراجعة")
            return@withContext false
        }

        // 4. Auto categorization check
        val learnedCategory = if (parsed.counterpartyNumber.isNotBlank()) {
            db.categoryRuleDao().getCategoryForPhone(activeUser.value, parsed.counterpartyNumber)
        } else null

        val finalCategory = learnedCategory ?: parsed.category

        // 5. Calculate cashier commission
        val commission = if (parsed.direction == "IN") {
            (parsed.amount * (targetWallet.receiveFeePct / 100.0)).coerceAtLeast(0.0)
        } else {
            (parsed.amount * (targetWallet.sendFeePct / 100.0)).coerceAtLeast(3.0)
        }

        // 6. Balance reconciliation
        val change = if (parsed.direction == "IN") parsed.amount else -(parsed.amount + parsed.fee)
        val computedBalance = (targetWallet.currentBalance + change).coerceAtLeast(0.0)
        val balanceAfter = parsed.balance ?: computedBalance

        if (parsed.balance != null && Math.abs(parsed.balance - computedBalance) > 0.01) {
            val mismatchFlag = FlagEntity(
                id = UUID.randomUUID().toString(),
                userId = activeUser.value,
                walletId = targetWallet.id,
                type = "BALANCE_MISMATCH",
                status = "OPEN",
                title = "فرق في رصيد ${targetWallet.providerName}",
                explanation = "الرصيد المذكور بالرسالة (${parsed.balance} ج.م) لا يطابق الرصيد المحسوب (${computedBalance} ج.م).",
                expectedBalance = computedBalance,
                actualBalance = parsed.balance,
                discrepancy = Math.abs(parsed.balance - computedBalance)
            )
            db.flagDao().insertFlag(mismatchFlag)
            AppNotificationCenter.post("فرق في الرصيد يحتاج مراجعة", "الرصيد الوارد في الرسالة يختلف عن الرصيد المحسوب. افتح التنبيهات للمراجعة.", "مراجعة")
        }

        // 7. Save transaction
        val tx = TransactionEntity(
            id = UUID.randomUUID().toString(),
            userId = activeUser.value,
            walletId = targetWallet.id,
            deviceId = "dev-cashier-1",
            fingerprint = fingerprint,
            direction = parsed.direction,
            type = if (parsed.direction == "IN") "TRANSFER" else "TRANSFER",
            amount = parsed.amount,
            fee = parsed.fee,
            cashierCommission = commission,
            balanceAfter = balanceAfter,
            counterpartyNumber = parsed.counterpartyNumber,
            counterpartyName = parsed.counterpartyName,
            operationCode = parsed.operationCode,
            occurredAt = System.currentTimeMillis(),
            status = "CONFIRMED",
            category = finalCategory,
            isAutoCategorized = learnedCategory != null,
            note = "",
            isManual = false,
            rawText = messageBody
        )

        db.transactionDao().insertTransaction(tx)
        db.walletDao().updateBalance(activeUser.value, targetWallet.id, balanceAfter)

        // 8. Sync online
        CloudSyncApi.syncTransaction(activeUser.value, tx)
        AppNotificationCenter.post("تم تسجيل معاملة من رسالة SMS", "تمت معالجة رسالة جديدة وتحديث دفتر المعاملات.", "SMS")

        return@withContext true
    }

    /**
     * Adds a manual transaction
     */
    suspend fun addManualTransaction(
        walletId: String,
        direction: String,
        amount: Double,
        counterpartyNumber: String,
        counterpartyName: String,
        category: String,
        commission: Double,
        note: String
    ): Boolean = withContext(Dispatchers.IO) {
        if (activeUser.value.isBlank()) return@withContext false
        val wallet = db.walletDao().getWalletByIdSync(activeUser.value, walletId) ?: return@withContext false
        val change = if (direction == "IN") amount else -amount
        val newBalance = (wallet.currentBalance + change).coerceAtLeast(0.0)

        val tx = TransactionEntity(
            id = UUID.randomUUID().toString(),
            userId = activeUser.value,
            walletId = walletId,
            deviceId = "dev-cashier-1",
            fingerprint = "MANUAL-${System.currentTimeMillis()}-$amount",
            direction = direction,
            type = "TRANSFER",
            amount = amount,
            fee = 0.0,
            cashierCommission = commission,
            balanceAfter = newBalance,
            counterpartyNumber = counterpartyNumber,
            counterpartyName = counterpartyName,
            operationCode = "MANUAL${System.currentTimeMillis() % 100000}",
            occurredAt = System.currentTimeMillis(),
            status = "MANUAL",
            category = category,
            note = note,
            isManual = true,
            rawText = "معاملة يدوية مسجلة بواسطة الكاشير: $amount ج.م - $note"
        )

        db.transactionDao().insertTransaction(tx)
        db.walletDao().updateBalance(activeUser.value, walletId, newBalance)
        CloudSyncApi.syncTransaction(activeUser.value, tx)
        AppNotificationCenter.post("تم حفظ المعاملة اليدوية", "تم حفظ المعاملة وتحديث رصيد المحفظة.", "معاملة")

        // Save counterparty category rule if name provided
        if (counterpartyNumber.isNotBlank() && category.isNotBlank()) {
            db.categoryRuleDao().insertRule(
                CategoryRuleEntity(
                    id = "cr-${counterpartyNumber}",
                    userId = activeUser.value,
                    counterpartyNumber = counterpartyNumber,
                    assignedCategory = category,
                    counterpartyName = counterpartyName
                )
            )
        }

        return@withContext true
    }

    /**
     * Saves a cashier shift/day closing
     */
    suspend fun closeShift(shiftClose: ShiftCloseEntity) = withContext(Dispatchers.IO) {
        if (activeUser.value.isNotBlank() && shiftClose.userId == activeUser.value) {
            db.shiftCloseDao().insertShiftClose(shiftClose)
            AppNotificationCenter.post("تم إقفال الوردية", "تم حفظ تقرير الإقفال اليومي بنجاح.", "وردية")
        }
    }
}
