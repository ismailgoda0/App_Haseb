package com.homecash.data.local.dao

import androidx.room.*
import com.homecash.data.local.entity.FlagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FlagDao {
    @Query("SELECT * FROM flags WHERE userId = :userId AND status = 'OPEN' ORDER BY createdAt DESC")
    fun getOpenFlags(userId: String = "admin"): Flow<List<FlagEntity>>

    @Query("SELECT COUNT(*) FROM flags WHERE userId = :userId AND status = 'OPEN'")
    fun getOpenFlagsCount(userId: String = "admin"): Flow<Int>

    @Query("SELECT * FROM flags WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllFlags(userId: String = "admin"): Flow<List<FlagEntity>>

    @Query("SELECT * FROM flags WHERE id = :id LIMIT 1")
    suspend fun getFlagById(id: String): FlagEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlag(flag: FlagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlags(flags: List<FlagEntity>)

    @Update
    suspend fun updateFlag(flag: FlagEntity)

    @Query("UPDATE flags SET status = 'RESOLVED', resolutionAction = :action, resolvedAt = :timestamp WHERE userId = :userId AND id = :id")
    suspend fun resolveFlag(userId: String, id: String, action: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE flags SET status = 'RESOLVED', resolutionAction = :action, resolvedAt = :timestamp WHERE userId = :userId AND type = :flagType AND status = 'OPEN'")
    suspend fun batchResolveFlags(userId: String = "admin", flagType: String, action: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM flags WHERE id = :id")
    suspend fun deleteFlag(id: String)
}
