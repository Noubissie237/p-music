package com.example.p_music.domain.repository

import com.example.p_music.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun getAllVideos(): Flow<List<Video>>
    suspend fun getVideoById(id: Long): Video?
    suspend fun toggleFavorite(video: Video)
    suspend fun isFavorite(videoId: Long): Boolean
    fun getAllFavorites(): Flow<List<Video>>
} 