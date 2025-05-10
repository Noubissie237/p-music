package com.example.p_music.domain.repository

import com.example.p_music.domain.model.Audio
import kotlinx.coroutines.flow.Flow

interface FavoriteAudioRepository {
    fun getAllFavorites(): Flow<List<Audio>>
    suspend fun toggleFavorite(audio: Audio)
    suspend fun isFavorite(audioId: Long): Boolean
} 