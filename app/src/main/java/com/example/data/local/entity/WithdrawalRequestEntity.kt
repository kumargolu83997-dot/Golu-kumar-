package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "withdrawal_requests")
data class WithdrawalRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val requestId: String,            // Unique ID e.g., "WDR-920184"
    val userId: String,               // Customer phone number
    val amount: Double,               // Minimum ₹100
    val method: String,               // "UPI" or "BANK"
    val upiId: String? = null,
    val accountNumber: String? = null,
    val ifscCode: String? = null,
    val accountHolderName: String? = null,
    val bankName: String? = null,
    val status: String = "Pending",   // "Pending", "Approved", "Paid", "Rejected"
    val payoutReference: String? = null, // Payout transaction confirmation ID
    val rejectionReason: String? = null,
    val requestTimestamp: Long = System.currentTimeMillis(),
    val processedTimestamp: Long? = null
)
