package com.homecash.data.local.dao

import androidx.room.*
import com.homecash.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY occurredAt DESC")
    fun getAllTransactions(userId: String = "admin"): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY occurredAt DESC LIMIT :limit")
    fun getRecentTransactions(userId: String = "admin", limit: Int = 10): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND walletId = :walletId ORDER BY occurredAt DESC")
    fun getTransactionsByWallet(userId: String = "admin", walletId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND (counterpartyNumber = :phoneOrName OR counterpartyName = :phoneOrName) ORDER BY occurredAt DESC")
    fun getTransactionsByCounterparty(userId: String = "admin", phoneOrName: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND (counterpartyNumber = :phoneOrName OR counterpartyName = :phoneOrName) ORDER BY occurredAt DESC")
    suspend fun getTransactionsByCounterpartySync(userId: String = "admin", phoneOrName: String): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND fingerprint = :fingerprint LIMIT 1")
    suspend fun getTransactionByFingerprint(userId: String, fingerprint: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE userId = :userId AND id = :id LIMIT 1")
    suspend fun getTransactionById(userId: String, id: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE userId = :userId AND occurredAt >= :startMillis AND occurredAt <= :endMillis ORDER BY occurredAt DESC")
    fun getTransactionsInDateRange(userId: String = "admin", startMillis: Long, endMillis: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND occurredAt >= :startMillis AND occurredAt <= :endMillis ORDER BY occurredAt DESC")
    suspend fun getTransactionsInDateRangeSync(userId: String = "admin", startMillis: Long, endMillis: Long): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND (counterpartyNumber LIKE '%' || :query || '%' OR counterpartyName LIKE '%' || :query || '%' OR operationCode LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%') ORDER BY occurredAt DESC")
    fun searchTransactions(userId: String = "admin", query: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
}
