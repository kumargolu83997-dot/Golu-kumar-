package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ticket_replies")
data class TicketReplyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ticketId: Long,
    val senderRole: String,           // "USER" or "ADMIN"
    val senderName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
