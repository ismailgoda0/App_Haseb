package com.homecash.data.local.dao

import androidx.room.*
import com.homecash.data.local.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices WHERE userId = :userId ORDER BY isThisDevice DESC, lastSyncAt DESC")
    fun getDevices(userId: String = "admin"): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM devices WHERE userId = :userId")
    suspend fun getDevicesSync(userId: String = "admin"): List<DeviceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<DeviceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity)

    @Update
    suspend fun updateDevice(device: DeviceEntity)

    @Query("UPDATE devices SET lastSyncAt = :timestamp, status = 'ONLINE' WHERE id = :deviceId")
    suspend fun updateDeviceSyncTime(deviceId: String, timestamp: Long = System.currentTimeMillis())
}
