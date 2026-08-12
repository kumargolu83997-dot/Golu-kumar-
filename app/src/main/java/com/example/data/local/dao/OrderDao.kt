package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY openedTimestamp DESC")
    fun getOrdersForUserFlow(userId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY openedTimestamp DESC")
    fun getAllOrdersFlow(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: Long): OrderEntity?

    @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber LIMIT 1")
    suspend fun getOrderByNumber(orderNumber: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status, orderAmount = :amount, calculatedReward = :reward, verifiedTimestamp = :verifiedTime, adminNotes = :notes WHERE id = :orderId")
    suspend fun updateOrderVerificationStatus(
        orderId: Long,
        status: String,
        amount: Double,
        reward: Double,
        verifiedTime: Long = System.currentTimeMillis(),
        notes: String? = null
    )

    @Query("UPDATE orders SET isRewardCredited = 1 WHERE id = :orderId")
    suspend fun markRewardCredited(orderId: Long)
}
