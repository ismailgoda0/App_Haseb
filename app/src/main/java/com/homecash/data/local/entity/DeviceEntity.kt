package com.homecash.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val deviceName: String,
    val isThisDevice: Boolean = false,
    val batteryOptDisabled: Boolean = true,
    val status: String = "ONLINE", // "ONLINE", "SYNCING", "OFFLINE"
    val lastSyncAt: Long = System.currentTimeMillis()
)
