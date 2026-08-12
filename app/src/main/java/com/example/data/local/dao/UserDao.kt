package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    fun getUserByPhoneFlow(phone: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET hashedPin = :hashedPin, pinSalt = :salt WHERE phone = :phone")
    suspend fun updateWithdrawalPin(phone: String, hashedPin: String, salt: String)

    @Query("UPDATE users SET balance = balance + :amount, totalEarned = totalEarned + :amount WHERE phone = :phone")
    suspend fun creditUserBalance(phone: String, amount: Double)

    @Query("UPDATE users SET balance = balance - :amount, totalWithdrawn = totalWithdrawn + :amount WHERE phone = :phone AND balance >= :amount")
    suspend fun debitUserBalanceForWithdrawal(phone: String, amount: Double): Int
}
