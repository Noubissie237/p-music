package com.example.p_music.domain.repository

import com.example.p_music.domain.model.Audio
import kotlinx.coroutines.flow.Flow

interface AudioRepository {
    suspend fun getAllAudios(): List<Audio>
    suspend fun getAudioById(id: Long): Audio?
    suspend fun getFavorites(): List<Audio>
    suspend fun getAudiosByArtist(artist: String): List<Audio>
    suspend fun getAudiosByAlbum(album: String): List<Audio>
    suspend fun toggleFavorite(audio: Audio)
    suspend fun updateAudioMetadata(audio: Audio)
    suspend fun deleteAudio(audio: Audio)
} 