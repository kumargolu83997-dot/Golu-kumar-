package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ticketNumber: String,          // e.g. "TCK-48201"
    val userId: String,                // Customer phone number
    val category: String,              // "Missing Reward", "Withdrawal Issue", "Order Status", "General Query"
    val subject: String,
    val initialMessage: String,
    val relatedOrderId: String? = null,
    val status: String = "Open",        // "Open", "In Progress", "Replied", "Closed"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
