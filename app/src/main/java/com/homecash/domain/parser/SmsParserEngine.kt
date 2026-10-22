package com.homecash.domain.parser

import java.util.Locale

object SmsParserEngine {

    /**
     * Converts Eastern Arabic numerals (٠-٩) and Arabic decimal comma (٫) to standard ASCII
     */
    fun normalizeArabicNumbers(input: String): String {
        val easternNumerals = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        var result = input
        for (i in easternNumerals.indices) {
            result = result.replace(easternNumerals[i], ('0'.code + i).toChar())
        }
        return result.replace('٫', '.')
    }

    /**
     * Main entry point to parse incoming SMS or Notification
     */
    fun parse(sender: String, rawMessage: String): ParsedSmsResult {
        val normalizedText = normalizeArabicNumbers(rawMessage)
        val fraudCheck = AntiFraudValidator.validateSender(sender, normalizedText)

        if (!fraudCheck.first) {
            return ParsedSmsResult(
                sender = sender,
                walletProvider = "غير معروف (تنبيه احتيال)",
                direction = "IN",
                amount = extractAmount(normalizedText) ?: 0.0,
                rawText = rawMessage,
                isRecognized = false,
                isPotentialFraud = true,
                fraudReason = fraudCheck.second
            )
        }

        val cleanSender = sender.trim().lowercase(Locale.ROOT)

        // 1. Vodafone Cash
        if (cleanSender.contains("vf") || cleanSender.contains("voda") || normalizedText.contains("فودافون كاش")) {
            parseVodafoneCash(sender, normalizedText, rawMessage)?.let { return it }
        }

        // 2. InstaPay
        if (cleanSender.contains("instapay") || cleanSender.contains("ipn") || normalizedText.contains("انستاباي") || normalizedText.contains("إنستاباي")) {
            parseInstaPay(sender, normalizedText, rawMessage)?.let { return it }
        }

        // 3. Etisalat Cash
        if (cleanSender.contains("etisalat") || cleanSender.contains("e&") || normalizedText.contains("اتصالات كاش")) {
            parseEtisalatCash(sender, normalizedText, rawMessage)?.let { return it }
        }

        // 4. Orange Cash
        if (cleanSender.contains("orange") || normalizedText.contains("أورنج كاش") || normalizedText.contains("اورنج كاش")) {
            parseOrangeCash(sender, normalizedText, rawMessage)?.let { return it }
        }

        // 5. WE Pay
        if (cleanSender.contains("we") || normalizedText.contains("وي باي") || normalizedText.contains("we pay")) {
            parseWePay(sender, normalizedText, rawMessage)?.let { return it }
        }

        // 6. Bank SMS (NBE, CIB, etc.)
        if (cleanSender.contains("nbe") || cleanSender.contains("cib") || cleanSender.contains("bm") || normalizedText.contains("البنك الأهلي") || normalizedText.contains("بنك مصر")) {
            parseBankSms(sender, normalizedText, rawMessage)?.let { return it }
        }

        // 7. Generic Fallback Parser
        return parseGeneric(sender, normalizedText, rawMessage)
    }

    private fun parseVodafoneCash(sender: String, text: String, raw: String): ParsedSmsResult? {
        val isReceived = text.contains("تم استلام") || text.contains("استلمت") || text.contains("تم إيداع")
        val isSent = text.contains("تم تحويل") || text.contains("تم سحب") || text.contains("تم خصم") || text.contains("تم دفع")
        val direction = if (isReceived) "IN" else "OUT"

        val amountRegex = Regex("""(?:مبلغ|استلام|تحويل|سحب|إيداع)\s*([\d,]+(?:\.\d+)?)\s*(?:ج\.م|جم|جنيه)?""")
        val amount = amountRegex.find(text)?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()
            ?: extractAmount(text) ?: return null

        val feeRegex = Regex("""(?:مصاريف الخدمة|الرسوم|العمولة)\s*([\d,]+(?:\.\d+)?)\s*(?:ج\.م|جم|جنيه)?""")
        val fee = feeRegex.find(text)?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull() ?: 0.0

        val balanceRegex = Regex("""(?:رصيدك الحالي هو|رصيدك الحالي|رصيدك الجديد|رصيدك)\s*([\d,]+(?:\.\d+)?)\s*(?:ج\.م|جم|جنيه)?""")
        val balance = balanceRegex.find(text)?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()

        val phoneRegex = Regex("""(?:من رقم|إلى رقم|من|إلى)\s*(01[0125]\d{8})""")
        val counterpartyNumber = phoneRegex.find(text)?.groupValues?.get(1) ?: ""

        val opCodeRegex = Regex("""(?:رقم العملية|كود العملية|العملية رقم|المرجع)[:\s]*([A-Za-z0-9]+)""")
        val opCode = opCodeRegex.find(text)?.groupValues?.get(1) ?: "VF${System.currentTimeMillis() % 1000000}"

        val category = when {
            text.contains("سحب") -> "سحب نقدي"
            text.contains("إيداع") -> "إيداع نقدي"
            text.contains("شحن") -> "شحن رصيد"
            text.contains("فاتورة") -> "فواتير"
            else -> "تحويل كاش"
        }

        return ParsedSmsResult(
            sender = sender,
            walletProvider = "فودافون كاش",
            direction = direction,
            amount = amount,
            fee = fee,
            balance = balance,
            counterpartyNumber = counterpartyNumber,
            operationCode = opCode,
            category = category,
            rawText = raw,
            isRecognized = true
        )
    }

    private fun parseInstaPay(sender: String, text: String, raw: String): ParsedSmsResult? {
        val isReceived = text.contains("تم استلام") || text.contains("استلمت") || text.contains("أودع")
        val isSent = text.contains("تم تحويل") || text.contains("تم إرسال") || text.contains("خصم")
        val direction = if (isReceived) "IN" else "OUT"

        val amountRegex = Regex("""(?:استلام|تحويل|إرسال|مبلغ)\s*([\d,]+(?:\.\d+)?)\s*(?:جم|ج\.م|جنيه)""")
        val amount = amountRegex.find(text)?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()
            ?: extractAmount(text) ?: return null

        val opCodeRegex = Regex("""(?:مرجع المعاملة|رقم المرجع|المرجع)[:\s]*([A-Za-z0-9]+)""")
        val opCode = opCodeRegex.find(text)?.groupValues?.get(1) ?: "IPN${System.currentTimeMillis() % 1000000}"

        // Counterparty name or account/handle (e.g. "من أحمد علي عبر انستاباي" or "إلى user@instapay")
        val nameRegex = Regex("""(?:من|إلى)\s+([^\d\n\r,]+?)\s+(?:عبر|بنجاح|على)""")
        val counterpartyName = nameRegex.find(text)?.groupValues?.get(1)?.trim() ?: ""

        val phoneRegex = Regex("""(01[0125]\d{8}|[a-zA-Z0-9._%+-]+@instapay)""")
        val counterpartyNumber = phoneRegex.find(text)?.groupValues?.get(1) ?: ""

        return ParsedSmsResult(
            sender = sender,
            walletProvider = "إنستاباي",
            direction = direction,
            amount = amount,
            fee = 0.0,
            balance = null, // InstaPay SMS does not always expose bank balance
            counterpartyNumber = counterpartyNumber,
            counterpartyName = counterpartyName,
            operationCode = opCode,
            category = "إنستاباي",
            rawText = raw,
            isRecognized = true
        )
    }

    private fun parseEtisalatCash(sender: String, text: String, raw: String): ParsedSmsResult? {
        val isReceived = text.contains("تم استلام") || text.contains("استلمت") || text.contains("إيداع")
        val direction = if (isReceived) "IN" else "OUT"

        val amountRegex = Regex("""(?:مبلغ|استلام|تحويل)\s*([\d,]+(?:\.\d+)?)\s*(?:ج\.م|جم|جنيه)?""")
        val amount = amountRegex.find(text)?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()
            ?: extractAmount(text) ?: return null

        val balanceRegex = Regex("""(?:رصيدك الحالي|رصيدك)\s*([\d,]+(?:\.\d+)?)""")
        val balance = balanceRegex.find(text)?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()

        val phoneRegex = Regex("""(?:من|إلى)\s*(01[0125]\d{8})""")
        val counterpartyNumber = phoneRegex.find(text)?.groupValues?.get(1) ?: ""

        val opCodeRegex = Regex("""(?:كود المعاملة|رقم العملية)[:\s]*([A-Za-z0-9]+)""")
        val opCode = opCodeRegex.find(text)?.groupValues?.get(1) ?: "EC${System.currentTimeMillis() % 1000000}"

        return ParsedSmsResult(
            sender = sender,
            walletProvider = "اتصالات كاش",
            direction = direction,
            amount = amount,
            balance = balance,
            counterpartyNumber = counterpartyNumber,
            operationCode = opCode,
            category = "تحويل كاش",
            rawText = raw,
            isRecognized = true
        )
    }

    private fun parseOrangeCash(sender: String, text: String, raw: String): ParsedSmsResult? {
        val isReceived = text.contains("تم استلام") || text.contains("إيداع")
        val direction = if (isReceived) "IN" else "OUT"

        val amount = extractAmount(text) ?: return null

        val balanceRegex = Regex("""(?:رصيدك الجديد|رصيدك)\s*([\d,]+(?:\.\d+)?)""")
        val balance = balanceRegex.find(text)?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()

        val phoneRegex = Regex("""(?:من|إلى)\s*(01[0125]\d{8})""")
        val counterpartyNumber = phoneRegex.find(text)?.groupValues?.get(1) ?: ""

        val opCodeRegex = Regex("""(?:رقم العملية|كود المعاملة)[:\s]*([A-Za-z0-9]+)""")
        val opCode = opCodeRegex.find(text)?.groupValues?.get(1) ?: "OC${System.currentTimeMillis() % 1000000}"

        return ParsedSmsResult(
            sender = sender,
            walletProvider = "أورنج كاش",
            direction = direction,
            amount = amount,
            balance = balance,
            counterpartyNumber = counterpartyNumber,
            operationCode = opCode,
            category = "تحويل كاش",
            rawText = raw,
            isRecognized = true
        )
    }

    private fun parseWePay(sender: String, text: String, raw: String): ParsedSmsResult? {
        val isReceived = text.contains("تم استلام") || text.contains("إيداع")
        val direction = if (isReceived) "IN" else "OUT"

        val amount = extractAmount(text) ?: return null

        val phoneRegex = Regex("""(01[0125]\d{8})""")
        val counterpartyNumber = phoneRegex.find(text)?.groupValues?.get(1) ?: ""

        val opCodeRegex = Regex("""(?:رقم العملية|المرجع)[:\s]*([A-Za-z0-9]+)""")
        val opCode = opCodeRegex.find(text)?.groupValues?.get(1) ?: "WE${System.currentTimeMillis() % 1000000}"

        return ParsedSmsResult(
            sender = sender,
            walletProvider = "وي باي",
            direction = direction,
            amount = amount,
            counterpartyNumber = counterpartyNumber,
            operationCode = opCode,
            category = "وي باي",
            rawText = raw,
            isRecognized = true
        )
    }

    private fun parseBankSms(sender: String, text: String, raw: String): ParsedSmsResult? {
        val isReceived = text.contains("تم إضافة") || text.contains("تم إيداع") || text.contains("تحويل وارد")
        val direction = if (isReceived) "IN" else "OUT"

        val amount = extractAmount(text) ?: return null

        val balanceRegex = Regex("""(?:الرصيد المتاح|رصيدك المتاح|الرصيد الحالي)\s*[:\s]*([\d,]+(?:\.\d+)?)""")
        val balance = balanceRegex.find(text)?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()

        val opCodeRegex = Regex("""(?:مرجع|رقم المرجع|Ref)[:\s]*([A-Za-z0-9]+)""")
        val opCode = opCodeRegex.find(text)?.groupValues?.get(1) ?: "BANK${System.currentTimeMillis() % 1000000}"

        return ParsedSmsResult(
            sender = sender,
            walletProvider = if (sender.contains("cib", true)) "بنك CIB" else "البنك الأهلي المصري",
            direction = direction,
            amount = amount,
            balance = balance,
            operationCode = opCode,
            category = "حساب بنكي",
            rawText = raw,
            isRecognized = true
        )
    }

    private fun parseGeneric(sender: String, text: String, raw: String): ParsedSmsResult {
        val isReceived = text.contains("استلام") || text.contains("إيداع") || text.contains("وارد")
        val direction = if (isReceived) "IN" else "OUT"
        val amount = extractAmount(text) ?: 0.0

        val phoneRegex = Regex("""(01[0125]\d{8})""")
        val counterpartyNumber = phoneRegex.find(text)?.groupValues?.get(1) ?: ""

        val isRecognized = amount > 0.0

        return ParsedSmsResult(
            sender = sender,
            walletProvider = if (isRecognized) "محفظة مالية" else "غير معروف",
            direction = direction,
            amount = amount,
            counterpartyNumber = counterpartyNumber,
            operationCode = "OP${System.currentTimeMillis() % 1000000}",
            category = "معاملة عامة",
            rawText = raw,
            isRecognized = isRecognized
        )
    }

    private fun extractAmount(text: String): Double? {
        // Regex looking for numbers followed or preceded by currency indicators
        val regex1 = Regex("""([\d,]+(?:\.\d+)?)\s*(?:ج\.م|جم|جنيه|EGP|LE)""", RegexOption.IGNORE_CASE)
        regex1.find(text)?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()?.let { return it }

        val regex2 = Regex("""(?:مبلغ|بقيمة|بمبلغ)\s*([\d,]+(?:\.\d+)?)""")
        regex2.find(text)?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()?.let { return it }

        return null
    }
}
