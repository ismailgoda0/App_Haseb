package com.homecash.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flags")
data class FlagEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val walletId: String,
    val transactionId: String? = null,
    val type: String, // "BALANCE_MISMATCH", "MISSING_TX", "FAILED_TX", "UNKNOWN_SMS", "UNREGISTERED_LINE", "DUPLICATE"
    val status: String = "OPEN", // "OPEN", "RESOLVED", "IGNORED"
    val title: String,
    val explanation: String,
    val expectedBalance: Double = 0.0,
    val actualBalance: Double = 0.0,
    val discrepancy: Double = 0.0,
    val resolutionAction: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null
)
