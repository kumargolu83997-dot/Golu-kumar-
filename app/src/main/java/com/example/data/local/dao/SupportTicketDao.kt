package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.SupportTicketEntity
import com.example.data.local.entity.TicketReplyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SupportTicketDao {
    @Query("SELECT * FROM support_tickets WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getTicketsForUserFlow(userId: String): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM support_tickets ORDER BY updatedAt DESC")
    fun getAllTicketsFlow(): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM support_tickets WHERE id = :id LIMIT 1")
    suspend fun getTicketById(id: Long): SupportTicketEntity?

    @Query("SELECT * FROM ticket_replies WHERE ticketId = :ticketId ORDER BY timestamp ASC")
    fun getRepliesForTicketFlow(ticketId: Long): Flow<List<TicketReplyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SupportTicketEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReply(reply: TicketReplyEntity): Long

    @Update
    suspend fun updateTicket(ticket: SupportTicketEntity)

    @Query("UPDATE support_tickets SET status = :status, updatedAt = :updatedAt WHERE id = :ticketId")
    suspend fun updateTicketStatus(ticketId: Long, status: String, updatedAt: Long = System.currentTimeMillis())
}
