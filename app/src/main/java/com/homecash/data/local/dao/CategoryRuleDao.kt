package com.homecash.data.local.dao

import androidx.room.*
import com.homecash.data.local.entity.CategoryRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryRuleDao {
    @Query("SELECT * FROM category_rules WHERE userId = :userId")
    fun getRules(userId: String = "admin"): Flow<List<CategoryRuleEntity>>

    @Query("SELECT assignedCategory FROM category_rules WHERE userId = :userId AND counterpartyNumber = :phone LIMIT 1")
    suspend fun getCategoryForPhone(userId: String = "admin", phone: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: CategoryRuleEntity)

    @Delete
    suspend fun deleteRule(rule: CategoryRuleEntity)
}
