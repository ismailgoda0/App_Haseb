package com.homecash.data.local.dao

import androidx.room.*
import com.homecash.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallets WHERE userId = :userId AND isActive = 1 ORDER BY currentBalance DESC")
    fun getWallets(userId: String = "admin"): Flow<List<WalletEntity>>

    @Query("SELECT * FROM wallets WHERE userId = :userId AND isActive = 1 ORDER BY currentBalance DESC")
    suspend fun getWalletsSync(userId: String = "admin"): List<WalletEntity>

    @Query("SELECT * FROM wallets WHERE id = :id AND userId = :userId LIMIT 1")
    fun getWalletById(userId: String, id: String): Flow<WalletEntity?>

    @Query("SELECT * FROM wallets WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getWalletByIdSync(userId: String, id: String): WalletEntity?

    @Query("SELECT * FROM wallets WHERE userId = :userId AND providerName = :name LIMIT 1")
    suspend fun getWalletByName(userId: String, name: String): WalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallets(wallets: List<WalletEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: WalletEntity)

    @Update
    suspend fun updateWallet(wallet: WalletEntity)

    @Query("UPDATE wallets SET currentBalance = :newBalance, lastUpdated = :timestamp WHERE id = :walletId AND userId = :userId")
    suspend fun updateBalance(userId: String, walletId: String, newBalance: Double, timestamp: Long = System.currentTimeMillis())
}
