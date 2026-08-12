package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marketplace_links")
data class MarketplaceLinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: String,       // Foreign key matching CategoryEntity id ("flipkart", "meesho", etc.)
    val title: String,            // Title of the offer/link e.g., "Electronics Sale - Extra Cashback"
    val targetUrl: String,        // Target URL to open
    val cashbackOfferText: String, // e.g. "Earn up to ₹100 Reward"
    val estimatedDays: String = "15-30 days", // Verification turnaround time
    val termsAndConditions: String,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
