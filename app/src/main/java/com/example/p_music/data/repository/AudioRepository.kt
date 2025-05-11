package com.example.p_music.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.p_music.domain.model.Audio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioRepository(private val context: Context) {
    
    suspend fun getAllAudios(): List<Audio> = withContext(Dispatchers.IO) {
        val audioList = mutableListOf<Audio>()
        
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_ADDED
        )
        
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        
        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val duration = cursor.getLong(durationColumn)
                val path = cursor.getString(pathColumn)
                val size = cursor.getLong(sizeColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)
                
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                
                val audio = Audio(
                    id = id,
                    title = title,
                    artist = artist,
                    album = album,
                    duration = duration,
                    uri = contentUri,
                    path = path,
                    coverUri = contentUri,
                    size = size,
                    dateAdded = dateAdded
                )
                
                audioList.add(audio)
            }
        }
        
        audioList
    }
} 