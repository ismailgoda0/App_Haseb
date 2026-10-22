package com.homecash.data.repository

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.homecash.data.model.ProviderColorSet
import com.homecash.data.model.ProviderInfo
import org.json.JSONArray
import java.io.InputStreamReader

object ProviderManager {

    private val fallbackProviders = listOf(
        ProviderInfo(
            id = "vodafone_cash",
            slug = "vodafone",
            nameAr = "فودافون كاش",
            nameEn = "Vodafone Cash",
            shortName = "VF Cash",
            badgeText = "VF",
            hotline = "7001",
            ussdCode = "*9#",
            defaultFeePercent = 1.0,
            feeDescriptionAr = "عمولة 1% بحد أدنى 3 ج.م وأقصى 10 ج.م على السحب",
            feeDescriptionEn = "1% fee with min 3 EGP and max 10 EGP on cashout",
            smsSenders = listOf("VF-Cash", "Vodafone", "VodafoneCash", "VFCash"),
            regexPattern = "فودافون كاش|VF-Cash|Vodafone",
            lightColors = ProviderColorSet(
                primary = Color(0xFFE60000),
                container = Color(0xFFFEECEB),
                onContainer = Color(0xFF8A0000),
                border = Color(0xFFF8B4AF)
            ),
            darkColors = ProviderColorSet(
                primary = Color(0xFFFF3B30),
                container = Color(0xFF3D0C0A),
                onContainer = Color(0xFFFFD1CF),
                border = Color(0xFF6E1B17)
            ),
            logoSvgPath = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 14.5c0 .83-.67 1.5-1.5 1.5S10 17.33 10 16.5v-5c0-.83.67-1.5 1.5-1.5s1.5.67 1.5 1.5v5zm3-2c0 .83-.67 1.5-1.5 1.5S13 15.33 13 14.5v-3c0-.83.67-1.5 1.5-1.5s1.5.67 1.5 1.5v3z"
        ),
        ProviderInfo(
            id = "instapay",
            slug = "instapay",
            nameAr = "إنستاباي",
            nameEn = "InstaPay (IPN)",
            shortName = "InstaPay",
            badgeText = "IP",
            hotline = "15997",
            ussdCode = "N/A",
            defaultFeePercent = 0.0,
            feeDescriptionAr = "تحويلات مجانية وفورية عبر شبكة المدفوعات اللحظية IPN",
            feeDescriptionEn = "Instant zero-fee bank & wallet transfers via IPN",
            smsSenders = listOf("InstaPay", "IPN", "EBC", "Instapay Egypt"),
            regexPattern = "إنستاباي|InstaPay|شبكة المدفوعات اللحظية|IPN",
            lightColors = ProviderColorSet(
                primary = Color(0xFF28004D),
                container = Color(0xFFF3EBF9),
                onContainer = Color(0xFF20003E),
                border = Color(0xFFD8BFED)
            ),
            darkColors = ProviderColorSet(
                primary = Color(0xFF8C43BA),
                container = Color(0xFF240E36),
                onContainer = Color(0xFFE8D3F5),
                border = Color(0xFF552175)
            ),
            logoSvgPath = "M13 3L4 14h6l-1 7 9-11h-6l1-7z"
        ),
        ProviderInfo(
            id = "etisalat_cash",
            slug = "etisalat",
            nameAr = "اتصالات كاش",
            nameEn = "Etisalat Cash (e&)",
            shortName = "e& Cash",
            badgeText = "e&",
            hotline = "778",
            ussdCode = "*777#",
            defaultFeePercent = 1.0,
            feeDescriptionAr = "عمولة 1% على السحب النقدي عبر منافذ اتصالات وATM",
            feeDescriptionEn = "1% cashout fee across e& outlets and ATMs",
            smsSenders = listOf("EtisalatCash", "Etisalat", "e&", "Etisalat Cash"),
            regexPattern = "اتصالات كاش|Etisalat Cash|e& cash|اتصالات",
            lightColors = ProviderColorSet(
                primary = Color(0xFF72BF44),
                container = Color(0xFFEFF9EB),
                onContainer = Color(0xFF285811),
                border = Color(0xFFBDE2A4)
            ),
            darkColors = ProviderColorSet(
                primary = Color(0xFF8DE05A),
                container = Color(0xFF18330D),
                onContainer = Color(0xFFD2F5BE),
                border = Color(0xFF36681E)
            ),
            logoSvgPath = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 15h-2v-6h2v6zm0-8h-2V7h2v2zm4 8h-2v-4h2v4zm0-6h-2v-2h2v2z"
        ),
        ProviderInfo(
            id = "orange_cash",
            slug = "orange",
            nameAr = "أورنج كاش",
            nameEn = "Orange Cash",
            shortName = "Orange",
            badgeText = "OG",
            hotline = "626",
            ussdCode = "*115#",
            defaultFeePercent = 1.0,
            feeDescriptionAr = "عمولة 1% على السحب لجميع فروع أورنج وماكينات الصراف",
            feeDescriptionEn = "1% cashout fee at Orange branches and ATMs",
            smsSenders = listOf("Orange Cash", "OrangeMoney", "Orange", "OrangeCash"),
            regexPattern = "أورنج كاش|Orange Cash|أورانج كاش|Orange",
            lightColors = ProviderColorSet(
                primary = Color(0xFFFF7900),
                container = Color(0xFFFFF3E6),
                onContainer = Color(0xFF8F4400),
                border = Color(0xFFFFC899)
            ),
            darkColors = ProviderColorSet(
                primary = Color(0xFFFFA040),
                container = Color(0xFF3D1C00),
                onContainer = Color(0xFFFFE5CC),
                border = Color(0xFF803E00)
            ),
            logoSvgPath = "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-2 10h-4v4h-2v-4H7v-2h4V7h2v4h4v2z"
        ),
        ProviderInfo(
            id = "we_pay",
            slug = "we",
            nameAr = "وي باي",
            nameEn = "WE Pay",
            shortName = "WE Pay",
            badgeText = "WE",
            hotline = "565",
            ussdCode = "*322#",
            defaultFeePercent = 1.0,
            feeDescriptionAr = "عمولة سحب 1% عبر منافذ سنترالات WE وماكينات البنوك",
            feeDescriptionEn = "1% fee on withdrawals via Telecom Egypt WE outlets",
            smsSenders = listOf("WEPay", "WE_Pay", "TelecomEgypt", "WE"),
            regexPattern = "وي باي|WE Pay|WE|المصرية للاتصالات",
            lightColors = ProviderColorSet(
                primary = Color(0xFF5C2D91),
                container = Color(0xFFF4EFFB),
                onContainer = Color(0xFF321156),
                border = Color(0xFFCBB3E6)
            ),
            darkColors = ProviderColorSet(
                primary = Color(0xFF9A63DB),
                container = Color(0xFF240E3B),
                onContainer = Color(0xFFECDDF9),
                border = Color(0xFF552985)
            ),
            logoSvgPath = "M12 3L2 12h3v8h6v-6h2v6h6v-8h3L12 3z"
        ),
        ProviderInfo(
            id = "fawry",
            slug = "fawry",
            nameAr = "فوري",
            nameEn = "Fawry",
            shortName = "Fawry",
            badgeText = "FW",
            hotline = "16421",
            ussdCode = "N/A",
            defaultFeePercent = 1.5,
            feeDescriptionAr = "مدفوعات فواتير وشحن رصيد وإيداع محافظ فورية",
            feeDescriptionEn = "Instant bill payments, vouchers & wallet topups",
            smsSenders = listOf("Fawry", "FawryPay", "myFawry"),
            regexPattern = "فوري|Fawry|FawryPay",
            lightColors = ProviderColorSet(
                primary = Color(0xFFF3A100),
                container = Color(0xFFFEF7E6),
                onContainer = Color(0xFF7A5100),
                border = Color(0xFFFBD98C)
            ),
            darkColors = ProviderColorSet(
                primary = Color(0xFFFFCB05),
                container = Color(0xFF3B2E05),
                onContainer = Color(0xFFFFF1B8),
                border = Color(0xFF755D08)
            ),
            logoSvgPath = "M13 2.05v3.03c3.39.49 6 3.39 6 6.92 0 .9-.18 1.75-.48 2.54l2.6 1.53c.56-1.24.88-2.62.88-4.07 0-5.18-3.95-9.45-9-9.92zM12 19c-3.87 0-7-3.13-7-7 0-3.53 2.61-6.43 6-6.92V2.05c-5.06.49-9 4.76-9 9.95 0 5.52 4.47 10 9.99 10 3.31 0 6.24-1.61 8.01-4.09l-2.45-1.45C16.14 17.94 14.21 19 12 19z"
        ),
        ProviderInfo(
            id = "cib_smart_wallet",
            slug = "cib",
            nameAr = "المحفظة الذكية CIB",
            nameEn = "CIB Smart Wallet",
            shortName = "CIB Wallet",
            badgeText = "CIB",
            hotline = "19666",
            ussdCode = "N/A",
            defaultFeePercent = 1.0,
            feeDescriptionAr = "المحفظة الذكية من البنك التجاري الدولي مصر CIB",
            feeDescriptionEn = "Smart Wallet by Commercial International Bank Egypt",
            smsSenders = listOf("CIB", "SmartWallet", "CIB-Egypt"),
            regexPattern = "CIB|Smart Wallet|المحفظة الذكية|التجاري الدولي",
            lightColors = ProviderColorSet(
                primary = Color(0xFF003A70),
                container = Color(0xFFE6F0FA),
                onContainer = Color(0xFF001D38),
                border = Color(0xFF99C0E6)
            ),
            darkColors = ProviderColorSet(
                primary = Color(0xFF3E8EE0),
                container = Color(0xFF0B223D),
                onContainer = Color(0xFFCBE1F7),
                border = Color(0xFF194A7D)
            ),
            logoSvgPath = "M4 10h3v7H4zM10.5 10h3v7h-3zM2 22h19v-3H2v3zm15-12h3v7h-3zM11.5 1L2 6v2h19V6l-9.5-5z"
        ),
        ProviderInfo(
            id = "bm_wallet",
            slug = "banque_misr",
            nameAr = "محفظة بنك مصر",
            nameEn = "Banque Misr BM Wallet",
            shortName = "BM Wallet",
            badgeText = "BM",
            hotline = "19888",
            ussdCode = "N/A",
            defaultFeePercent = 1.0,
            feeDescriptionAr = "محفظة بنك مصر الإلكترونية للمعاملات والتحويلات",
            feeDescriptionEn = "BM Wallet by Banque Misr for quick mobile banking",
            smsSenders = listOf("BMWallet", "BanqueMisr", "BM_Wallet"),
            regexPattern = "بنك مصر|BM Wallet|Banque Misr",
            lightColors = ProviderColorSet(
                primary = Color(0xFF941D2B),
                container = Color(0xFFFAECEE),
                onContainer = Color(0xFF520B13),
                border = Color(0xFFE5A4AC)
            ),
            darkColors = ProviderColorSet(
                primary = Color(0xFFE04859),
                container = Color(0xFF3D0B10),
                onContainer = Color(0xFFFAD0D5),
                border = Color(0xFF851926)
            ),
            logoSvgPath = "M11.5 1L2 6v2h19V6l-9.5-5zM4 10h3v7H4zm6.5 0h3v7h-3zm6.5 0h3v7h-3zM2 22h19v-3H2v3z"
        ),
        ProviderInfo(
            id = "nbe_phonecash",
            slug = "nbe",
            nameAr = "فون كاش البنك الأهلي",
            nameEn = "NBE PhoneCash",
            shortName = "PhoneCash",
            badgeText = "NBE",
            hotline = "19623",
            ussdCode = "N/A",
            defaultFeePercent = 1.0,
            feeDescriptionAr = "خدمة فون كاش من البنك الأهلي المصري للأفراد والتجار",
            feeDescriptionEn = "PhoneCash mobile wallet by National Bank of Egypt",
            smsSenders = listOf("NBE", "PhoneCash", "NBE-Egypt"),
            regexPattern = "الأهلي|فون كاش|PhoneCash|NBE|البنك الأهلي",
            lightColors = ProviderColorSet(
                primary = Color(0xFF006837),
                container = Color(0xFFE6F6ED),
                onContainer = Color(0xFF00361C),
                border = Color(0xFF92D8B3)
            ),
            darkColors = ProviderColorSet(
                primary = Color(0xFF27B56C),
                container = Color(0xFF062E18),
                onContainer = Color(0xFFC8F4DC),
                border = Color(0xFF0D5B31)
            ),
            logoSvgPath = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"
        )
    )

    private var providersList: List<ProviderInfo> = fallbackProviders

    fun init(context: Context) {
        try {
            val jsonString = context.assets.open("providers.json").use { stream ->
                InputStreamReader(stream).readText()
            }
            val jsonArray = JSONArray(jsonString)
            val parsedList = mutableListOf<ProviderInfo>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getString("id")
                val slug = obj.optString("slug", id)
                val nameAr = obj.getString("name_ar")
                val nameEn = obj.getString("name_en")
                val shortName = obj.optString("short_name", nameEn)
                val badgeText = obj.optString("badge_text", id.take(2).uppercase())
                val hotline = obj.optString("hotline", "")
                val ussdCode = obj.optString("ussd_code", "")
                val feePercent = obj.optDouble("default_fee_percent", 1.0)
                val feeAr = obj.optString("fee_description_ar", "")
                val feeEn = obj.optString("fee_description_en", "")

                val sendersArray = obj.getJSONArray("sms_senders")
                val senders = mutableListOf<String>()
                for (s in 0 until sendersArray.length()) {
                    senders.add(sendersArray.getString(s))
                }
                val regexPattern = obj.optString("regex_pattern", "")

                val colorsObj = obj.getJSONObject("colors")
                val lightObj = colorsObj.getJSONObject("light")
                val darkObj = colorsObj.getJSONObject("dark")

                fun parseColor(hex: String): Color {
                    val clean = hex.removePrefix("#")
                    val full = if (clean.length == 6) "FF$clean" else clean
                    return Color(full.toLong(16))
                }

                val lightColors = ProviderColorSet(
                    primary = parseColor(lightObj.getString("primary")),
                    container = parseColor(lightObj.getString("container")),
                    onContainer = parseColor(lightObj.getString("on_container")),
                    border = parseColor(lightObj.getString("border"))
                )

                val darkColors = ProviderColorSet(
                    primary = parseColor(darkObj.getString("primary")),
                    container = parseColor(darkObj.getString("container")),
                    onContainer = parseColor(darkObj.getString("on_container")),
                    border = parseColor(darkObj.getString("border"))
                )

                val logoSvg = obj.optString("logo_svg_path", "")

                parsedList.add(
                    ProviderInfo(
                        id = id,
                        slug = slug,
                        nameAr = nameAr,
                        nameEn = nameEn,
                        shortName = shortName,
                        badgeText = badgeText,
                        hotline = hotline,
                        ussdCode = ussdCode,
                        defaultFeePercent = feePercent,
                        feeDescriptionAr = feeAr,
                        feeDescriptionEn = feeEn,
                        smsSenders = senders,
                        regexPattern = regexPattern,
                        lightColors = lightColors,
                        darkColors = darkColors,
                        logoSvgPath = logoSvg
                    )
                )
            }
            if (parsedList.isNotEmpty()) {
                providersList = parsedList
            }
        } catch (_: Exception) {
            // Safe fallback keeps default robust providers
        }
    }

    fun getAll(): List<ProviderInfo> = providersList

    fun getById(id: String): ProviderInfo {
        val normalized = id.lowercase().trim()
        return providersList.firstOrNull {
            it.id.equals(normalized, ignoreCase = true) ||
            it.slug.equals(normalized, ignoreCase = true) ||
            it.shortName.equals(normalized, ignoreCase = true) ||
            normalized.contains(it.slug) ||
            it.slug.contains(normalized)
        } ?: fallbackProviders.first()
    }

    fun findBySenderOrText(text: String): ProviderInfo? {
        val lower = text.lowercase()
        return providersList.firstOrNull { provider ->
            provider.smsSenders.any { sender -> lower.contains(sender.lowercase()) } ||
            lower.contains(provider.nameAr) ||
            lower.contains(provider.nameEn.lowercase()) ||
            lower.contains(provider.slug)
        }
    }
}
