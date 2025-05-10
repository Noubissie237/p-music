package com.example.p_music.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.p_music.data.local.entity.FavoriteVideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteVideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteVideoEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteVideoEntity)

    @Query("SELECT * FROM favorite_videos ORDER BY dateFavorited DESC")
    fun getAllFavorites(): Flow<List<FavoriteVideoEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_videos WHERE videoId = :videoId)")
    suspend fun isFavorite(videoId: Long): Boolean

    @Query("SELECT * FROM favorite_videos WHERE videoId = :videoId")
    suspend fun getFavoriteById(videoId: Long): FavoriteVideoEntity?
} 