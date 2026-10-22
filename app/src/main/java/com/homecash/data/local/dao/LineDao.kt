package com.homecash.data.local.dao

import androidx.room.*
import com.homecash.data.local.entity.LineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LineDao {
    @Query("SELECT * FROM lines WHERE userId = :userId")
    fun getLines(userId: String = "admin"): Flow<List<LineEntity>>

    @Query("SELECT * FROM lines WHERE userId = :userId")
    suspend fun getLinesSync(userId: String = "admin"): List<LineEntity>

    @Query("SELECT * FROM lines WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getLineByPhone(phone: String): LineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLines(lines: List<LineEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLine(line: LineEntity)

    @Delete
    suspend fun deleteLine(line: LineEntity)
}
