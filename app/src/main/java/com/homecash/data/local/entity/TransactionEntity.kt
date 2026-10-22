package com.homecash.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["userId", "fingerprint"], unique = true),
        Index(value = ["walletId"]),
        Index(value = ["counterpartyNumber"]),
        Index(value = ["occurredAt"])
    ]
)
data class TransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val walletId: String,
    val deviceId: String,
    val smsId: String? = null,
    val fingerprint: String,
    val direction: String, // "IN" (وارد) or "OUT" (صادر)
    val type: String = "TRANSFER", // "TRANSFER", "CASH_IN", "CASH_OUT", "PURCHASE", "BILL"
    val amount: Double,
    val fee: Double = 0.0, // مصاريف خدمة الشركة المذكورة في SMS
    val cashierCommission: Double = 0.0, // عمولة المحل/الكاشير
    val balanceAfter: Double = 0.0,
    val counterpartyNumber: String = "",
    val counterpartyName: String = "",
    val operationCode: String = "",
    val occurredAt: Long = System.currentTimeMillis(),
    val status: String = "CONFIRMED", // "CONFIRMED", "FLAGGED", "MANUAL", "RECONCILED"
    val category: String = "تحويل كاش",
    val isAutoCategorized: Boolean = false,
    val note: String = "",
    val isManual: Boolean = false,
    val rawText: String = "",
    val isSynced: Boolean = true
)
