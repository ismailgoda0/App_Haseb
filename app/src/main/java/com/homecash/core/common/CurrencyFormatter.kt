package com.homecash.core.common

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyFormatter {

    private val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
    }

    private val formatter = DecimalFormat("#,##0.00", symbols)
    private val integerFormatter = DecimalFormat("#,##0", symbols)

    fun format(amount: Double, showCurrency: Boolean = true): String {
        val formatted = formatter.format(amount)
        return if (showCurrency) "$formatted ج.م" else formatted
    }

    fun formatInteger(amount: Double, showCurrency: Boolean = true): String {
        val formatted = integerFormatter.format(amount)
        return if (showCurrency) "$formatted ج.م" else formatted
    }
}
