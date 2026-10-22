package com.homecash.data.local.dao

import androidx.room.*
import com.homecash.data.local.entity.ShiftCloseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftCloseDao {
    @Query("SELECT * FROM shift_closes WHERE userId = :userId ORDER BY closedAt DESC")
    fun getAllShiftCloses(userId: String = "admin"): Flow<List<ShiftCloseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShiftClose(shiftClose: ShiftCloseEntity)
}
