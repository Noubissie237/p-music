package com.example.p_music.data.repository

import com.example.p_music.data.local.dao.PlaylistDao
import com.example.p_music.data.local.entity.PlaylistAudioCrossRef
import com.example.p_music.data.local.entity.PlaylistEntity
import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.model.Playlist
import com.example.p_music.domain.repository.AudioRepository
import com.example.p_music.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val audioRepository: AudioRepository
) : PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylistsWithAudioIds().map { playlistsWithIds ->
            playlistsWithIds.map { playlistWithIds ->
                Playlist(
                    id = playlistWithIds.playlist.id,
                    name = playlistWithIds.playlist.name,
                    description = playlistWithIds.playlist.description,
                    audioCount = playlistWithIds.audioRefs.size,
                    createdAt = playlistWithIds.playlist.createdAt,
                    updatedAt = playlistWithIds.playlist.updatedAt,
                    audios = emptyList() // Les audios seront chargés séparément si nécessaire
                )
            }
        }
    }

    override fun getPlaylistById(playlistId: Long): Flow<Playlist?> {
        return playlistDao.getPlaylistWithAudioIds(playlistId).map { playlistWithIds ->
            playlistWithIds?.let {
                // Charger tous les audios
                val allAudios = audioRepository.getAudios().firstOrNull() ?: emptyList()
                
                // Filtrer les audios de cette playlist
                val audioIds = it.audioRefs.map { ref -> ref.audioId }.toSet()
                val playlistAudios = allAudios.filter { audio -> audio.id in audioIds }
                
                Playlist(
                    id = it.playlist.id,
                    name = it.playlist.name,
                    description = it.playlist.description,
                    audioCount = it.audioRefs.size,
                    createdAt = it.playlist.createdAt,
                    updatedAt = it.playlist.updatedAt,
                    audios = playlistAudios
                )
            }
        }
    }

    override suspend fun createPlaylist(name: String, description: String): Long {
        val playlist = PlaylistEntity(
            name = name,
            description = description
        )
        return playlistDao.insertPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            createdAt = playlist.createdAt,
            updatedAt = System.currentTimeMillis()
        )
        playlistDao.updatePlaylist(entity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val entity = PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            createdAt = playlist.createdAt,
            updatedAt = playlist.updatedAt
        )
        playlistDao.deletePlaylist(entity)
    }

    override suspend fun addAudioToPlaylist(playlistId: Long, audio: Audio) {
        val position = playlistDao.getPlaylistAudioCount(playlistId)
        val crossRef = PlaylistAudioCrossRef(
            playlistId = playlistId,
            audioId = audio.id,
            position = position
        )
        playlistDao.addAudioToPlaylist(crossRef)
    }

    override suspend fun removeAudioFromPlaylist(playlistId: Long, audioId: String) {
        playlistDao.removeAudioFromPlaylistById(playlistId, audioId)
    }

    override suspend fun isAudioInPlaylist(playlistId: Long, audioId: String): Boolean {
        return playlistDao.isAudioInPlaylist(playlistId, audioId) > 0
    }
}
