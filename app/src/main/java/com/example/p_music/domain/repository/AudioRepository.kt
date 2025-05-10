package com.example.p_music.domain.repository

import com.example.p_music.domain.model.Audio
import kotlinx.coroutines.flow.Flow

interface AudioRepository {
    fun getAllAudios(): Flow<List<Audio>>
    fun getAudioById(id: Long): Flow<Audio?>
    fun getFavorites(): Flow<List<Audio>>
    fun getAudiosByArtist(artist: String): Flow<List<Audio>>
    fun getAudiosByAlbum(album: String): Flow<List<Audio>>
    suspend fun toggleFavorite(audio: Audio)
    suspend fun updateAudioMetadata(audio: Audio)
    suspend fun deleteAudio(audio: Audio)
} 