package com.homecash.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "admin",
    val email: String = "admin@homecash.app",
    val name: String = "أحمد محمود (المدير)",
    val mode: String = "business", // "personal" or "business"
    val businessName: String = "محل الباشا فون",
    val businessType: String = "محل موبايلات وخدمات دفع",
    val monthlyBudget: Double = 15000.0,
    val pinCode: String? = "1234",
    val isDemoMode: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
