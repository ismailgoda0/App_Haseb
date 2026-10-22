package com.homecash.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shift_closes")
data class ShiftCloseEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val deviceId: String,
    val cashierName: String = "كاشير الوردية الأولى",
    val openedAt: Long,
    val closedAt: Long = System.currentTimeMillis(),
    val expectedCash: Double,
    val actualCash: Double,
    val difference: Double, // actualCash - expectedCash (عجز أو زيادة)
    val totalInward: Double,
    val totalOutward: Double,
    val totalCommission: Double,
    val notes: String = ""
)
