package com.homecash.domain.model

import com.homecash.data.local.entity.TransactionEntity

data class CounterpartySummary(
    val counterpartyNumber: String,
    val counterpartyName: String,
    val totalSent: Double = 0.0,
    val totalReceived: Double = 0.0,
    val netBalance: Double = 0.0, // totalReceived - totalSent
    val totalCommissions: Double = 0.0,
    val transactionCount: Int = 0,
    val transactions: List<TransactionEntity> = emptyList()
)
