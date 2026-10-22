package com.homecash.domain.parser

import org.junit.Assert.*
import org.junit.Test

class SmsParserEngineTest {

    @Test
    fun testNormalizeArabicNumbers() {
        val easternArabic = "مبلغ ٥٠0٫٥٠ جنيه"
        val normalized = SmsParserEngine.normalizeArabicNumbers(easternArabic)
        assertEquals("مبلغ 500.50 جنيه", normalized)
    }

    @Test
    fun testVodafoneCashIncomingSms() {
        val sender = "VF-Cash"
        val body = "تم استلام مبلغ 500.00 جم من رقم 01012345678. رصيدك الحالي هو 1250.50 جم. رقم العملية: 987654"
        val result = SmsParserEngine.parse(sender, body)

        assertTrue(result.isRecognized)
        assertFalse(result.isPotentialFraud)
        assertEquals("فودافون كاش", result.walletProvider)
        assertEquals("IN", result.direction)
        assertEquals(500.00, result.amount, 0.001)
        assertEquals(1250.50, result.balance ?: 0.0, 0.001)
        assertEquals("01012345678", result.counterpartyNumber)
        assertEquals("987654", result.operationCode)
    }

    @Test
    fun testVodafoneCashOutgoingSms() {
        val sender = "VF-Cash"
        val body = "تم تحويل مبلغ 200 جم إلى رقم 01198765432. مصاريف الخدمة 3 جم. رصيدك الحالي هو 1050.50 جم. رقم العملية: 112233"
        val result = SmsParserEngine.parse(sender, body)

        assertTrue(result.isRecognized)
        assertEquals("فودافون كاش", result.walletProvider)
        assertEquals("OUT", result.direction)
        assertEquals(200.0, result.amount, 0.001)
        assertEquals(3.0, result.fee, 0.001)
        assertEquals(1050.50, result.balance ?: 0.0, 0.001)
        assertEquals("01198765432", result.counterpartyNumber)
        assertEquals("112233", result.operationCode)
    }

    @Test
    fun testInstaPaySms() {
        val sender = "InstaPay"
        val body = "تم استلام مبلغ 1500.00 جم من Mohamed Ali عبر انستاباي. مرجع المعاملة: IPN998877"
        val result = SmsParserEngine.parse(sender, body)

        assertTrue(result.isRecognized)
        assertEquals("إنستاباي", result.walletProvider)
        assertEquals("IN", result.direction)
        assertEquals(1500.00, result.amount, 0.001)
        assertEquals("IPN998877", result.operationCode)
    }

    @Test
    fun testEtisalatCashSms() {
        val sender = "EtisalatCash"
        val body = "تم استلام مبلغ 350 جم من 01112223334. رصيدك الحالي 850 جم. كود المعاملة: EC4455"
        val result = SmsParserEngine.parse(sender, body)

        assertTrue(result.isRecognized)
        assertEquals("اتصالات كاش", result.walletProvider)
        assertEquals("IN", result.direction)
        assertEquals(350.0, result.amount, 0.001)
        assertEquals("01112223334", result.counterpartyNumber)
        assertEquals("EC4455", result.operationCode)
    }

    @Test
    fun testBankSmsNbe() {
        val sender = "NBE"
        val body = "تم إضافة مبلغ 2500.00 جم تحويل وارد إلى حسابكم. الرصيد المتاح: 15250.00 جم. مرجع: Ref12345"
        val result = SmsParserEngine.parse(sender, body)

        assertTrue(result.isRecognized)
        assertEquals("البنك الأهلي المصري", result.walletProvider)
        assertEquals("IN", result.direction)
        assertEquals(2500.0, result.amount, 0.001)
        assertEquals(15250.0, result.balance ?: 0.0, 0.001)
        assertEquals("Ref12345", result.operationCode)
    }
}
