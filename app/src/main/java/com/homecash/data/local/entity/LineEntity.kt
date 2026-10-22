package com.homecash.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lines")
data class LineEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val deviceId: String,
    val phoneNumber: String,
    val carrier: String, // "Vodafone", "Etisalat", "Orange", "WE"
    val displayName: String,
    val simSlotIndex: Int = 0,
    val isPrimary: Boolean = true
)
