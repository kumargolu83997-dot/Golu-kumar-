package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val amount: Double,               // Positive for CREDIT, Negative/Positive amount with type
    val type: String,                 // "CREDIT_REWARD", "WITHDRAWAL_REQUEST", "WITHDRAWAL_REFUND"
    val status: String,               // "COMPLETED", "PENDING", "FAILED"
    val referenceId: String,          // e.g. Order Number or Withdrawal ID
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
