package com.example.p_music.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.repository.AudioRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.Duration
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioRepository {

    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.DATE_ADDED
    )

    override fun getAllAudios(): Flow<List<Audio>> = flow {
        val audioList = mutableListOf<Audio>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                createAudioFromCursor(cursor)?.let { audioList.add(it) }
            }
        }
        emit(audioList)
    }.flowOn(Dispatchers.IO)

    override fun getAudioById(id: Long): Flow<Audio?> = flow<Audio?> {
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media._ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                emit(createAudioFromCursor(cursor))
            } else {
                emit(null)
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun getFavorites(): Flow<List<Audio>> = flow<List<Audio>> {
        // TODO: Implémenter la gestion des favoris avec Room
        emit(emptyList())
    }.flowOn(Dispatchers.IO)

    override fun getAudiosByArtist(artist: String): Flow<List<Audio>> = flow {
        val audioList = mutableListOf<Audio>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media.ARTIST} = ?"
        val selectionArgs = arrayOf(artist)

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                createAudioFromCursor(cursor)?.let { audioList.add(it) }
            }
        }
        emit(audioList)
    }.flowOn(Dispatchers.IO)

    override fun getAudiosByAlbum(album: String): Flow<List<Audio>> = flow {
        val audioList = mutableListOf<Audio>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media.ALBUM} = ?"
        val selectionArgs = arrayOf(album)

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                createAudioFromCursor(cursor)?.let { audioList.add(it) }
            }
        }
        emit(audioList)
    }.flowOn(Dispatchers.IO)

    override suspend fun toggleFavorite(audio: Audio) {
        // TODO: Implémenter la gestion des favoris avec Room
    }

    override suspend fun updateAudioMetadata(audio: Audio) {
        // TODO: Implémenter la mise à jour des métadonnées
    }

    override suspend fun deleteAudio(audio: Audio) {
        context.contentResolver.delete(audio.uri, null, null)
    }

    private fun getAlbumArtUri(albumId: Long): Uri? {
        val albumArtUri = ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            albumId
        )
        return try {
            context.contentResolver.openInputStream(albumArtUri)?.use {
                albumArtUri
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun createAudioFromCursor(cursor: Cursor): Audio? {
        return try {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
            val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
            val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
            val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
            val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
            val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
            val dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))

            val contentUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                id
            )

            Audio(
                id = id,
                title = title,
                artist = artist,
                album = album,
                duration = Duration.ofMillis(duration),
                uri = contentUri,
                coverUri = getAlbumArtUri(id),
                path = path,
                size = size,
                dateAdded = dateAdded
            )
        } catch (e: Exception) {
            null
        }
    }
} 