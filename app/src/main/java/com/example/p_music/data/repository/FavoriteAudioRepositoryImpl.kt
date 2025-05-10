package com.example.p_music.data.repository

import com.example.p_music.data.local.dao.FavoriteAudioDao
import com.example.p_music.data.local.entity.FavoriteAudioEntity
import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.repository.FavoriteAudioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteAudioRepositoryImpl @Inject constructor(
    private val favoriteAudioDao: FavoriteAudioDao
) : FavoriteAudioRepository {

    override fun getAllFavorites(): Flow<List<Audio>> {
        return favoriteAudioDao.getAllFavorites().map { entities ->
            entities.map { it.toAudio() }
        }
    }

    override suspend fun toggleFavorite(audio: Audio) {
        if (favoriteAudioDao.isFavorite(audio.id)) {
            favoriteAudioDao.getFavoriteById(audio.id)?.let {
                favoriteAudioDao.deleteFavorite(it)
            }
        } else {
            favoriteAudioDao.insertFavorite(audio.toEntity())
        }
    }

    override suspend fun isFavorite(audioId: Long): Boolean {
        return favoriteAudioDao.isFavorite(audioId)
    }

    private fun FavoriteAudioEntity.toAudio() = Audio(
        id = audioId,
        title = title,
        artist = artist,
        album = album,
        duration = java.time.Duration.ofMillis(duration),
        uri = android.net.Uri.parse(uri),
        coverUri = coverUri?.let { android.net.Uri.parse(it) },
        path = path,
        size = size,
        dateAdded = dateAdded,
        isFavorite = true
    )

    private fun Audio.toEntity() = FavoriteAudioEntity(
        audioId = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration.toMillis(),
        uri = uri.toString(),
        coverUri = coverUri?.toString(),
        path = path,
        size = size,
        dateAdded = dateAdded
    )
} 