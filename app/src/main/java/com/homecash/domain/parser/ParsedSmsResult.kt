package com.homecash.domain.parser

data class ParsedSmsResult(
    val sender: String,
    val walletProvider: String,
    val direction: String, // "IN" or "OUT"
    val amount: Double,
    val fee: Double = 0.0,
    val balance: Double? = null,
    val counterpartyNumber: String = "",
    val counterpartyName: String = "",
    val operationCode: String = "",
    val category: String = "تحويل كاش",
    val rawText: String,
    val isRecognized: Boolean = true,
    val isPotentialFraud: Boolean = false,
    val fraudReason: String? = null
)
