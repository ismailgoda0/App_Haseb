package com.homecash.data.model

import androidx.compose.ui.graphics.Color

data class ProviderColorSet(
    val primary: Color,
    val container: Color,
    val onContainer: Color,
    val border: Color
)

data class ProviderInfo(
    val id: String,
    val slug: String,
    val nameAr: String,
    val nameEn: String,
    val shortName: String,
    val badgeText: String,
    val hotline: String,
    val ussdCode: String,
    val defaultFeePercent: Double,
    val feeDescriptionAr: String,
    val feeDescriptionEn: String,
    val smsSenders: List<String>,
    val regexPattern: String,
    val lightColors: ProviderColorSet,
    val darkColors: ProviderColorSet,
    val logoSvgPath: String
) {
    fun getColors(isDark: Boolean): ProviderColorSet = if (isDark) darkColors else lightColors
}
