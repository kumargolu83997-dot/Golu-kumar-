package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.WithdrawalRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WithdrawalRequestDao {
    @Query("SELECT * FROM withdrawal_requests WHERE userId = :userId ORDER BY requestTimestamp DESC")
    fun getWithdrawalRequestsForUserFlow(userId: String): Flow<List<WithdrawalRequestEntity>>

    @Query("SELECT * FROM withdrawal_requests ORDER BY requestTimestamp DESC")
    fun getAllWithdrawalRequestsFlow(): Flow<List<WithdrawalRequestEntity>>

    @Query("SELECT * FROM withdrawal_requests WHERE id = :id LIMIT 1")
    suspend fun getWithdrawalRequestById(id: Long): WithdrawalRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawalRequest(request: WithdrawalRequestEntity): Long

    @Update
    suspend fun updateWithdrawalRequest(request: WithdrawalRequestEntity)

    @Query("UPDATE withdrawal_requests SET status = :status, payoutReference = :payoutRef, rejectionReason = :reason, processedTimestamp = :processedTime WHERE id = :requestId")
    suspend fun updateWithdrawalStatus(
        requestId: Long,
        status: String,
        payoutRef: String? = null,
        reason: String? = null,
        processedTime: Long = System.currentTimeMillis()
    )
}
