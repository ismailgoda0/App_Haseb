package com.homecash.data.local.dao

import androidx.room.*
import com.homecash.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUser(id: String = "admin"): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserSync(id: String = "admin"): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET mode = :mode WHERE id = :id")
    suspend fun updateMode(id: String = "admin", mode: String)
}
