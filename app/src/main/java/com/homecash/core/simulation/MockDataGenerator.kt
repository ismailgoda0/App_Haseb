package com.homecash.core.simulation

import com.homecash.data.local.AppDatabase
import com.homecash.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

object MockDataGenerator {

    suspend fun seedDatabaseIfEmpty(db: AppDatabase) = withContext(Dispatchers.IO) {
        val existingUser = db.userDao().getUserSync("admin")
        if (existingUser != null) return@withContext

        // 1. Admin User
        val adminUser = UserEntity(
            id = "admin",
            email = "admin@homecash.app",
            name = "أحمد محمود (المدير العام)",
            mode = "business", // default to business cashier mode
            businessName = "محل الباشا فون",
            businessType = "محل موبايلات وتحويلات مالية",
            monthlyBudget = 25000.0,
            pinCode = "1234",
            isDemoMode = true
        )
        db.userDao().insertUser(adminUser)

        // 2. 3 Linked Devices
        val devices = listOf(
            DeviceEntity(
                id = "dev-cashier-1",
                userId = "admin",
                deviceName = "هاتف المحل الرئيسي - كاشير 1",
                isThisDevice = true,
                batteryOptDisabled = true,
                status = "ONLINE",
                lastSyncAt = System.currentTimeMillis()
            ),
            DeviceEntity(
                id = "dev-cashier-2",
                userId = "admin",
                deviceName = "هاتف الفرع الثاني - كاشير 2",
                isThisDevice = false,
                batteryOptDisabled = true,
                status = "ONLINE",
                lastSyncAt = System.currentTimeMillis() - 120000
            ),
            DeviceEntity(
                id = "dev-owner-3",
                userId = "admin",
                deviceName = "هاتف الإدارة - موبايل المالك",
                isThisDevice = false,
                batteryOptDisabled = true,
                status = "ONLINE",
                lastSyncAt = System.currentTimeMillis() - 300000
            )
        )
        db.deviceDao().insertDevices(devices)

        // 3. SIM Lines
        val lines = listOf(
            LineEntity("line-voda-1", "admin", "dev-cashier-1", "01023455842", "Vodafone", "شريحة كاشير 1 - فودافون", 0, true),
            LineEntity("line-etisalat-1", "admin", "dev-cashier-1", "01145678901", "Etisalat", "شريحة كاشير 1 - اتصالات", 1, false),
            LineEntity("line-orange-2", "admin", "dev-cashier-2", "01234567890", "Orange", "شريحة كاشير 2 - أورنج", 0, true),
            LineEntity("line-we-2", "admin", "dev-cashier-2", "01598765432", "WE", "شريحة كاشير 2 - وي", 1, false)
        )
        db.lineDao().insertLines(lines)

        // 4. Wallets
        val wallets = listOf(
            WalletEntity(
                id = "w-voda-main",
                userId = "admin",
                lineId = "line-voda-1",
                providerName = "فودافون كاش",
                currentBalance = 14850.0,
                initialBalance = 10000.0,
                sendFeePct = 1.0,
                receiveFeePct = 0.5,
                colorHex = "#E60000"
            ),
            WalletEntity(
                id = "w-instapay-main",
                userId = "admin",
                lineId = "line-voda-1",
                providerName = "إنستاباي",
                currentBalance = 32400.0,
                initialBalance = 25000.0,
                sendFeePct = 0.0,
                receiveFeePct = 0.0,
                colorHex = "#7B2CBF"
            ),
            WalletEntity(
                id = "w-etisalat-1",
                userId = "admin",
                lineId = "line-etisalat-1",
                providerName = "اتصالات كاش",
                currentBalance = 8200.0,
                initialBalance = 5000.0,
                sendFeePct = 1.0,
                receiveFeePct = 0.5,
                colorHex = "#78BE20"
            ),
            WalletEntity(
                id = "w-orange-2",
                userId = "admin",
                lineId = "line-orange-2",
                providerName = "أورنج كاش",
                currentBalance = 6750.0,
                initialBalance = 5000.0,
                sendFeePct = 1.0,
                receiveFeePct = 0.5,
                colorHex = "#FF7900"
            )
        )
        db.walletDao().insertWallets(wallets)

        // 5. Initial Transactions (Representing multiple phones)
        val now = System.currentTimeMillis()
        val hour = 3600000L

        val initialTransactions = listOf(
            TransactionEntity(
                id = "tx-1",
                userId = "admin",
                walletId = "w-voda-main",
                deviceId = "dev-cashier-1",
                fingerprint = "VF9812401-500",
                direction = "IN",
                type = "TRANSFER",
                amount = 500.0,
                fee = 0.0,
                cashierCommission = 5.0,
                balanceAfter = 14850.0,
                counterpartyNumber = "01012345678",
                counterpartyName = "أحمد فتحي",
                operationCode = "VF9812401",
                occurredAt = now - (15 * 60000), // 15 mins ago
                status = "CONFIRMED",
                category = "تحويل كاش",
                note = "استلام تحويل عميل لشراء شاحن",
                rawText = "تم استلام مبلغ 500.00 ج.م من رقم 01012345678 على محفظة فودافون كاش. مصاريف الخدمة 0 ج.م. رصيدك الحالي هو 14,850.00 ج.م. رقم العملية: VF9812401"
            ),
            TransactionEntity(
                id = "tx-2",
                userId = "admin",
                walletId = "w-voda-main",
                deviceId = "dev-cashier-1",
                fingerprint = "VF9812390-1000",
                direction = "OUT",
                type = "TRANSFER",
                amount = 1000.0,
                fee = 1.0,
                cashierCommission = 10.0,
                balanceAfter = 14350.0,
                counterpartyNumber = "01098765432",
                counterpartyName = "سارة مجدي",
                operationCode = "VF9812390",
                occurredAt = now - (1 * hour),
                status = "CONFIRMED",
                category = "تحويل كاش",
                note = "سحب كاش من المحل",
                rawText = "تم تحويل مبلغ 1000.00 ج.م إلى رقم 01098765432. مصاريف الخدمة 1.00 ج.م. رصيدك الحالي هو 14,350.00 ج.م. رقم العملية: VF9812390"
            ),
            TransactionEntity(
                id = "tx-3",
                userId = "admin",
                walletId = "w-instapay-main",
                deviceId = "dev-cashier-2", // Received on Phone 2
                fingerprint = "IPN20241021001-2500",
                direction = "IN",
                type = "TRANSFER",
                amount = 2500.0,
                fee = 0.0,
                cashierCommission = 15.0,
                balanceAfter = 32400.0,
                counterpartyNumber = "01123456789",
                counterpartyName = "محمود عبد الله",
                operationCode = "IPN20241021001",
                occurredAt = now - (2 * hour),
                status = "CONFIRMED",
                category = "إنستاباي",
                note = "دفع فاتورة صيانة من فرع 2",
                rawText = "تم استلام 2500.00 جم في حسابك من محمود عبد الله عبر انستاباي. مرجع المعاملة: IPN20241021001"
            ),
            TransactionEntity(
                id = "tx-4",
                userId = "admin",
                walletId = "w-etisalat-1",
                deviceId = "dev-cashier-1",
                fingerprint = "EC881234-300",
                direction = "IN",
                type = "TRANSFER",
                amount = 300.0,
                fee = 0.0,
                cashierCommission = 5.0,
                balanceAfter = 8200.0,
                counterpartyNumber = "01187654321",
                counterpartyName = "مصطفى كمال",
                operationCode = "EC881234",
                occurredAt = now - (4 * hour),
                status = "CONFIRMED",
                category = "تحويل كاش",
                note = "",
                rawText = "تم استلام مبلغ 300.00 ج.م من 01187654321. رصيدك الحالي 8,200.00 ج.م. كود المعاملة: EC881234"
            ),
            TransactionEntity(
                id = "tx-5",
                userId = "admin",
                walletId = "w-orange-2",
                deviceId = "dev-cashier-2", // Phone 2
                fingerprint = "OC554123-650",
                direction = "IN",
                type = "TRANSFER",
                amount = 650.0,
                fee = 0.0,
                cashierCommission = 7.0,
                balanceAfter = 6750.0,
                counterpartyNumber = "01234567890",
                counterpartyName = "علي حسن",
                operationCode = "OC554123",
                occurredAt = now - (6 * hour),
                status = "CONFIRMED",
                category = "تحويل كاش",
                note = "تحويل كاش فرع 2",
                rawText = "تم استلام مبلغ 650.00 جنيه مصري من 01234567890. رصيدك الجديد 6,750.00 جنيه. رقم العملية: OC554123"
            ),
            TransactionEntity(
                id = "tx-6",
                userId = "admin",
                walletId = "w-voda-main",
                deviceId = "dev-owner-3", // Phone 3 (Owner)
                fingerprint = "VF772390-2000",
                direction = "OUT",
                type = "CASH_OUT",
                amount = 2000.0,
                fee = 10.0,
                cashierCommission = 20.0,
                balanceAfter = 12340.0,
                counterpartyNumber = "01033445566",
                counterpartyName = "طارق يحيى",
                operationCode = "VF772390",
                occurredAt = now - (10 * hour),
                status = "FLAGGED", // Flagged for balance difference
                category = "سحب نقدي",
                note = "سحب مبلغ كبير - فودافون كاش",
                rawText = "تم سحب مبلغ 2000.00 ج.م من محفظتك بنجاح. مصاريف الخدمة 10.00 ج.م. رصيدك الحالي هو 12,340.00 ج.م. رقم العملية: VF772390"
            )
        )
        db.transactionDao().insertTransactions(initialTransactions)

        // 6. Seed Flags
        val flags = listOf(
            FlagEntity(
                id = "flag-1",
                userId = "admin",
                walletId = "w-voda-main",
                transactionId = "tx-6",
                type = "BALANCE_MISMATCH",
                status = "OPEN",
                title = "فرق في الرصيد — فودافون كاش",
                explanation = "رصيد فودافون كاش لا يطابق آخر رسالة. الفارق المحسوب: 1,000.00 ج.م. يرجى مراجعة المعاملات أو تسوية الفارق.",
                expectedBalance = 13850.0,
                actualBalance = 14850.0,
                discrepancy = 1000.0,
                createdAt = now - (30 * 60000)
            ),
            FlagEntity(
                id = "flag-2",
                userId = "admin",
                walletId = "w-instapay-main",
                transactionId = "tx-3",
                type = "MISSING_TX",
                status = "OPEN",
                title = "معاملة جديدة من فرع 2 تحتاج تأكيد عمولة",
                explanation = "تم تسجيل عملية استلام بقيمة 2,500.00 ج.م من كاشير الفرع الثاني بدون تحديد نسبة الخصم.",
                expectedBalance = 32400.0,
                actualBalance = 32400.0,
                discrepancy = 0.0,
                createdAt = now - (45 * 60000)
            )
        )
        db.flagDao().insertFlags(flags)

        // 7. Seed Counterparty Category Rules
        val rules = listOf(
            CategoryRuleEntity("cr-1", "admin", "01012345678", "عملاء المحل المميزين", "أحمد فتحي"),
            CategoryRuleEntity("cr-2", "admin", "01098765432", "تحويل كاش دوري", "سارة مجدي"),
            CategoryRuleEntity("cr-3", "admin", "01123456789", "صيانة وأجهزة", "محمود عبد الله")
        )
        for (r in rules) {
            db.categoryRuleDao().insertRule(r)
        }
    }
}
