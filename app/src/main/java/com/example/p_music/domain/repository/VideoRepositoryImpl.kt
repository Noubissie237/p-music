package com.example.p_music.domain.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.p_music.domain.model.Video
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : VideoRepository {

    override fun getAllVideos(): Flow<List<Video>> = flow {
        val videos = mutableListOf<Video>()
        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Video.Media.DURATION} > 0"
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
            val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val displayName = cursor.getString(displayNameColumn)
                val duration = cursor.getLong(durationColumn)
                val path = cursor.getString(dataColumn)
                val size = cursor.getLong(sizeColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)

                // Récupérer la miniature
                val thumbnailUri = ContentUris.withAppendedId(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    id
                )

                val video = Video(
                    id = id,
                    title = title,
                    displayName = displayName,
                    duration = java.time.Duration.ofMillis(duration),
                    uri = ContentUris.withAppendedId(collection, id),
                    thumbnailUri = thumbnailUri,
                    path = path,
                    size = size,
                    dateAdded = dateAdded,
                    isFavorite = false
                )
                videos.add(video)
            }
        }
        emit(videos)
    }.flowOn(Dispatchers.IO)

    override suspend fun getVideoById(id: Long): Video {
        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED
        )
        val selection = "${MediaStore.Video.Media._ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        return context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val displayName = cursor.getString(displayNameColumn)
                val duration = cursor.getLong(durationColumn)
                val path = cursor.getString(dataColumn)
                val size = cursor.getLong(sizeColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)

                // Récupérer la miniature
                val thumbnailUri = ContentUris.withAppendedId(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    id
                )

                Video(
                    id = id,
                    title = title,
                    displayName = displayName,
                    duration = java.time.Duration.ofMillis(duration),
                    uri = ContentUris.withAppendedId(collection, id),
                    thumbnailUri = thumbnailUri,
                    path = path,
                    size = size,
                    dateAdded = dateAdded,
                    isFavorite = false
                )
            } else {
                throw Exception("Vidéo non trouvée")
            }
        } ?: throw Exception("Erreur lors de la récupération de la vidéo")
    }

    override fun getAllFavorites(): Flow<List<Video>> = flow {
        // TODO: Implémenter la récupération des favoris
        emit(emptyList())
    }

    override suspend fun toggleFavorite(video: Video) {
        // TODO: Implémenter la gestion des favoris
    }

    override suspend fun isFavorite(videoId: Long): Boolean {
        // TODO: Implémenter la logique réelle des favoris
        return false
    }
} 