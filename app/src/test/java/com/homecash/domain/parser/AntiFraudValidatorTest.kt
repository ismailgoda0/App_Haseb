package com.homecash.domain.parser

import org.junit.Assert.*
import org.junit.Test

class AntiFraudValidatorTest {

    @Test
    fun testOfficialSenderPasses() {
        val result = AntiFraudValidator.validateSender("VF-Cash", "تم استلام مبلغ 100 جم")
        assertTrue(result.first)
        assertNull(result.second)
    }

    @Test
    fun testMobileNumberClaimingWalletIsFraud() {
        val fakeMobileSender = "01012345678"
        val fakeWalletBody = "تم تحويل 500 جم فودافون كاش إلى حسابك"
        val result = AntiFraudValidator.validateSender(fakeMobileSender, fakeWalletBody)

        assertFalse(result.first)
        assertNotNull(result.second)
        assertTrue(result.second!!.contains("احتيال محتمل"))
    }

    @Test
    fun testTyposquattingSenderIsFraud() {
        val fakeSender = "vodafone_cash_fake"
        val body = "تم تحويل 1000 جم"
        val result = AntiFraudValidator.validateSender(fakeSender, body)

        assertFalse(result.first)
        assertNotNull(result.second)
        assertTrue(result.second!!.contains("منتحلًا"))
    }
}
