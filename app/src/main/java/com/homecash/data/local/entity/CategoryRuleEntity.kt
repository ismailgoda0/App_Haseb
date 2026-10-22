package com.homecash.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_rules")
data class CategoryRuleEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val counterpartyNumber: String,
    val assignedCategory: String,
    val counterpartyName: String = ""
)
