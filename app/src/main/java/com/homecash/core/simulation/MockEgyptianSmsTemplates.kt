package com.homecash.core.simulation

import kotlin.random.Random

data class MockSmsScenario(
    val sender: String,
    val text: String,
    val deviceName: String,
    val walletProvider: String
)

object MockEgyptianSmsTemplates {

    private val CUSTOMER_NAMES = listOf(
        "أحمد فتحي", "محمد إبراهيم", "محمود عبد الله", "كريم عادل",
        "سارة مجدي", "مصطفى كمال", "علي حسن", "طارق يحيى",
        "عمر فاروق", "إسلام سعيد", "مينا سامي", "شريف نبيل"
    )

    private val PHONES = listOf(
        "01012345678", "01098765432", "01123456789", "01187654321",
        "01234567890", "01287654321", "01512345678", "01033445566"
    )

    private val DEVICES = listOf(
        "هاتف المحل الرئيسي - كاشير 1",
        "هاتف الفرع الثاني - كاشير 2",
        "هاتف الإدارة - موبايل المالك"
    )

    fun generateRandomScenario(currentBalance: Double): MockSmsScenario {
        val device = DEVICES.random()
        val customer = CUSTOMER_NAMES.random()
        val phone = PHONES.random()
        val opCode = Random.nextInt(10000000, 99999999).toString()
        val amounts = listOf(50.0, 100.0, 150.0, 200.0, 300.0, 500.0, 750.0, 1000.0, 1500.0, 2000.0)
        val amount = amounts.random()

        val type = Random.nextInt(1, 5)
        return when (type) {
            1 -> { // Vodafone Cash In
                val newBal = currentBalance + amount
                MockSmsScenario(
                    sender = "VF-Cash",
                    text = "تم استلام مبلغ ${"%.2f".format(amount)} ج.م من رقم $phone على محفظة فودافون كاش. مصاريف الخدمة 0 ج.م. رصيدك الحالي هو ${"%.2f".format(newBal)} ج.م. رقم العملية: $opCode",
                    deviceName = device,
                    walletProvider = "فودافون كاش"
                )
            }
            2 -> { // Vodafone Cash Out
                val fee = 1.0
                val newBal = (currentBalance - amount - fee).coerceAtLeast(0.0)
                MockSmsScenario(
                    sender = "VF-Cash",
                    text = "تم تحويل مبلغ ${"%.2f".format(amount)} ج.م إلى رقم $phone. مصاريف الخدمة 1.00 ج.م. رصيدك الحالي هو ${"%.2f".format(newBal)} ج.م. رقم العملية: $opCode",
                    deviceName = device,
                    walletProvider = "فودافون كاش"
                )
            }
            3 -> { // InstaPay In
                MockSmsScenario(
                    sender = "InstaPay",
                    text = "تم استلام ${"%.2f".format(amount)} جم في حسابك من $customer عبر انستاباي. مرجع المعاملة: IPN$opCode",
                    deviceName = device,
                    walletProvider = "إنستاباي"
                )
            }
            4 -> { // Etisalat Cash In
                val newBal = currentBalance + amount
                MockSmsScenario(
                    sender = "EtisalatCash",
                    text = "تم استلام مبلغ ${"%.2f".format(amount)} ج.م من $phone. رصيدك الحالي ${"%.2f".format(newBal)} ج.م. كود المعاملة: EC$opCode",
                    deviceName = device,
                    walletProvider = "اتصالات كاش"
                )
            }
            else -> { // Orange Cash In
                val newBal = currentBalance + amount
                MockSmsScenario(
                    sender = "OrangeCash",
                    text = "تم استلام مبلغ ${"%.2f".format(amount)} جنيه مصري من $phone. رصيدك الجديد ${"%.2f".format(newBal)} جنيه. رقم العملية: OC$opCode",
                    deviceName = device,
                    walletProvider = "أورنج كاش"
                )
            }
        }
    }
}
