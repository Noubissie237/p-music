package com.example.p_music.domain.repository

import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(playlistId: Long): Flow<Playlist?>
    suspend fun createPlaylist(name: String, description: String = ""): Long
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun addAudioToPlaylist(playlistId: Long, audio: Audio)
    suspend fun removeAudioFromPlaylist(playlistId: Long, audioId: String)
    suspend fun isAudioInPlaylist(playlistId: Long, audioId: String): Boolean
}
