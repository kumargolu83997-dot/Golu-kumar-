package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String, // e.g. "flipkart", "meesho", "amazon", "myntra"
    val name: String,           // Display name e.g. "Order Flipkart"
    val slug: String,
    val description: String,
    val brandColorHex: String,
    val iconName: String,
    val rewardNotice: String,
    val isActive: Boolean = true
)
