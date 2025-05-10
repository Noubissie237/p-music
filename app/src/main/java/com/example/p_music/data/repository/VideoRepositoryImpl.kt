package com.example.p_music.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.p_music.data.local.dao.FavoriteVideoDao
import com.example.p_music.data.local.entity.FavoriteVideoEntity
import com.example.p_music.domain.model.Video
import com.example.p_music.domain.repository.VideoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Duration
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val favoriteVideoDao: FavoriteVideoDao
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

        val selection = "${MediaStore.Video.Media.MIME_TYPE} LIKE ?"
        val selectionArgs = arrayOf("video/%")

        withContext(Dispatchers.IO) {
            context.contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                "${MediaStore.Video.Media.DATE_ADDED} DESC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val duration = cursor.getLong(durationColumn)
                    val path = cursor.getString(pathColumn)
                    val size = cursor.getLong(sizeColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val thumbnailUri = ContentUris.withAppendedId(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val video = Video(
                        id = id,
                        title = title,
                        displayName = displayName,
                        duration = Duration.ofMillis(duration),
                        uri = contentUri,
                        thumbnailUri = thumbnailUri,
                        path = path,
                        size = size,
                        dateAdded = dateAdded,
                        isFavorite = favoriteVideoDao.isFavorite(id)
                    )
                    videos.add(video)
                }
            }
        }
        emit(videos)
    }

    override suspend fun getVideoById(id: Long): Video? {
        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Video.Media._ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED
        )

        return withContext(Dispatchers.IO) {
            context.contentResolver.query(
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
                    val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                    val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val duration = cursor.getLong(durationColumn)
                    val path = cursor.getString(pathColumn)
                    val size = cursor.getLong(sizeColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val thumbnailUri = ContentUris.withAppendedId(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        id
                    )

                    Video(
                        id = id,
                        title = title,
                        displayName = displayName,
                        duration = Duration.ofMillis(duration),
                        uri = contentUri,
                        thumbnailUri = thumbnailUri,
                        path = path,
                        size = size,
                        dateAdded = dateAdded,
                        isFavorite = favoriteVideoDao.isFavorite(id)
                    )
                } else null
            }
        }
    }

    override suspend fun toggleFavorite(video: Video) {
        if (favoriteVideoDao.isFavorite(video.id)) {
            favoriteVideoDao.getFavoriteById(video.id)?.let {
                favoriteVideoDao.deleteFavorite(it)
            }
        } else {
            favoriteVideoDao.insertFavorite(video.toEntity())
        }
    }

    override suspend fun isFavorite(videoId: Long): Boolean {
        return favoriteVideoDao.isFavorite(videoId)
    }

    override fun getAllFavorites(): Flow<List<Video>> {
        return favoriteVideoDao.getAllFavorites().map { entities ->
            entities.map { it.toVideo() }
        }
    }

    private fun Video.toEntity() = FavoriteVideoEntity(
        videoId = id,
        title = title,
        displayName = displayName,
        duration = duration.toMillis(),
        uri = uri.toString(),
        thumbnailUri = thumbnailUri?.toString(),
        path = path,
        size = size,
        dateAdded = dateAdded
    )

    private fun FavoriteVideoEntity.toVideo() = Video(
        id = videoId,
        title = title,
        displayName = displayName,
        duration = Duration.ofMillis(duration),
        uri = Uri.parse(uri),
        thumbnailUri = thumbnailUri?.let { Uri.parse(it) },
        path = path,
        size = size,
        dateAdded = dateAdded,
        isFavorite = true
    )
} 