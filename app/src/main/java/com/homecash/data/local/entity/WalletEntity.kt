package com.homecash.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val lineId: String,
    val providerName: String, // "فودافون كاش", "إنستاباي", "اتصالات كاش", "أورنج كاش", "وي باي", "البنك الأهلي"
    val currentBalance: Double,
    val initialBalance: Double,
    val sendFeePct: Double = 1.0, // 1%
    val sendFeeFixed: Double = 0.0,
    val receiveFeePct: Double = 0.0,
    val receiveFeeFixed: Double = 0.0,
    val colorHex: String = "#4F63E8",
    val isActive: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)
