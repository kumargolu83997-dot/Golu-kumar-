package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String,          // e.g. "ORD-849201"
    val userId: String,               // Customer's phone number
    val categoryId: String,           // "flipkart", "meesho", "amazon", "myntra"
    val categoryName: String,
    val linkId: Long,
    val linkTitle: String,
    val targetUrl: String,
    val orderAmount: Double = 0.0,     // Order purchase value in ₹ (filled upon verification)
    val calculatedReward: Double = 0.0,// ₹4 for ₹100-999, ₹100 for ₹1000+
    val status: String = "Reward Processing", // "Reward Processing", "Received", "Rejected", "Cancelled"
    val isRewardCredited: Boolean = false, // Flag ensuring reward is credited EXACTLY ONCE
    val openedTimestamp: Long = System.currentTimeMillis(),
    val verifiedTimestamp: Long? = null,
    val adminNotes: String? = null
)
