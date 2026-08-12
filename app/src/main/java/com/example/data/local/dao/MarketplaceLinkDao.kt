package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.MarketplaceLinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketplaceLinkDao {
    @Query("SELECT * FROM marketplace_links WHERE categoryId = :categoryId AND isActive = 1 ORDER BY id DESC")
    fun getActiveLinksForCategoryFlow(categoryId: String): Flow<List<MarketplaceLinkEntity>>

    @Query("SELECT * FROM marketplace_links WHERE categoryId = :categoryId ORDER BY id DESC")
    fun getAllLinksForCategoryFlow(categoryId: String): Flow<List<MarketplaceLinkEntity>>

    @Query("SELECT * FROM marketplace_links ORDER BY id DESC")
    fun getAllLinksFlow(): Flow<List<MarketplaceLinkEntity>>

    @Query("SELECT * FROM marketplace_links WHERE id = :id LIMIT 1")
    suspend fun getLinkById(id: Long): MarketplaceLinkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: MarketplaceLinkEntity): Long

    @Update
    suspend fun updateLink(link: MarketplaceLinkEntity)

    @Query("DELETE FROM marketplace_links WHERE id = :id")
    suspend fun deleteLinkById(id: Long)
}
