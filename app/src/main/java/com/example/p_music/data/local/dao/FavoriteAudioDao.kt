package com.example.p_music.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.p_music.data.local.entity.FavoriteAudioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteAudioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteAudioEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteAudioEntity)

    @Query("SELECT * FROM favorite_audios ORDER BY dateFavorited DESC")
    fun getAllFavorites(): Flow<List<FavoriteAudioEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_audios WHERE audioId = :audioId)")
    suspend fun isFavorite(audioId: Long): Boolean

    @Query("SELECT * FROM favorite_audios WHERE audioId = :audioId")
    suspend fun getFavoriteById(audioId: Long): FavoriteAudioEntity?
} 