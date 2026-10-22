package com.homecash.domain.parser

object AntiFraudValidator {

    // Valid registered Sender IDs for Egyptian Wallets and Banks
    private val LEGITIMATE_SENDERS = setOf(
        "VF-Cash", "VF-CASH", "VodafoneCash", "Vodafone",
        "InstaPay", "INSTAPAY", "IPN",
        "EtisalatCash", "etisalat cash", "e& money", "e&money", "Etisalat",
        "OrangeCash", "Orange Cash", "Orange",
        "WE Pay", "WEPay", "WE", "TelecomEgypt",
        "NBE", "CIB", "BM", "Banque Misr", "QNB", "QNBAA", "AlexBank", "BDC"
    )

    /**
     * Checks if the incoming SMS originates from a verified official sender
     * and flags any attempt of regular mobile numbers pretending to be a wallet.
     */
    fun validateSender(sender: String, messageBody: String): Pair<Boolean, String?> {
        val cleanSender = sender.trim()

        // 1. If sender is a regular mobile number (e.g., starts with +201 or 010, 011, 012, 015)
        // but the message body contains wallet claim ("تم تحويل", "فودافون كاش", "انستاباي")
        val isMobileNumber = cleanSender.matches(Regex("""^(\+?20)?0?1[0125]\d{8}$"""))
        if (isMobileNumber) {
            val containsWalletClaim = messageBody.contains("كاش") || 
                                     messageBody.contains("فودافون") || 
                                     messageBody.contains("انستاباي") ||
                                     messageBody.contains("رصيدك") ||
                                     messageBody.contains("تم استلام")
            if (containsWalletClaim) {
                return Pair(false, "احتيال محتمل: الرسالة واردة من رقم هاتف عادي ($sender) وليس من اسم الشركة الرسمي")
            }
        }

        // 2. Exact or normalized matching against legitimate financial providers
        val isSenderOfficial = LEGITIMATE_SENDERS.any { it.equals(cleanSender, ignoreCase = true) }
        if (!isSenderOfficial) {
            // Check if sender has suspicious similarity (typosquatting)
            val suspiciousMatches = listOf("vodafone", "vfcash", "instapay", "etisalat", "orange")
            val lowerSender = cleanSender.lowercase()
            for (kw in suspiciousMatches) {
                if (lowerSender.contains(kw) && !LEGITIMATE_SENDERS.contains(cleanSender)) {
                    return Pair(false, "احتيال محتمل: اسم المرسل ($sender) غير معتمد رسميًا وقد يكون منتحلًا لصفة الشركة")
                }
            }
        }

        return Pair(true, null)
    }
}
