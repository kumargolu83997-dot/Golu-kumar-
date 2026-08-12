package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val phone: String, // Cleaned 10-digit mobile number
    val name: String = "Customer",
    val hashedPin: String? = null, // Hashed 4-8 digit withdrawal PIN
    val pinSalt: String? = null,   // Cryptographic salt for PIN
    val balance: Double = 0.0,     // Current wallet balance in INR (₹)
    val totalEarned: Double = 0.0,  // Cumulative total cashback rewards earned
    val totalWithdrawn: Double = 0.0, // Cumulative withdrawn amount
    val createdAt: Long = System.currentTimeMillis()
)
