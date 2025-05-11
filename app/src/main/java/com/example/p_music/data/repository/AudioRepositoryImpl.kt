package com.example.p_music.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.repository.AudioRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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

    override suspend fun getAllAudios(): List<Audio> = withContext(Dispatchers.IO) {
        val audioList = mutableListOf<Audio>()
        
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        Log.d("AudioRepository", "URI de collection: $collection")
        
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        Log.d("AudioRepository", "Sélection: $selection")
        
        try {
            // Vérifier si le ContentResolver est disponible
            if (context.contentResolver == null) {
                Log.e("AudioRepository", "ContentResolver est null!")
                return@withContext emptyList()
            }

            // Vérifier si l'URI est accessible
            try {
                context.contentResolver.getType(collection)
                Log.d("AudioRepository", "URI est accessible")
            } catch (e: Exception) {
                Log.e("AudioRepository", "URI n'est pas accessible: ${e.message}")
            }

            context.contentResolver.query(
                collection,
                projection,
                selection,
                null,
                null
            )?.use { cursor ->
                Log.d("AudioRepository", "Nombre de fichiers trouvés: ${cursor.count}")
                
                if (cursor.count == 0) {
                    Log.d("AudioRepository", "Aucun fichier trouvé. Vérifiez les permissions et les dossiers.")
                }
                
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                
                while (cursor.moveToNext()) {
                    try {
                        val id = cursor.getLong(idColumn)
                        val title = cursor.getString(titleColumn)
                        val artist = cursor.getString(artistColumn)
                        val album = cursor.getString(albumColumn)
                        val duration = cursor.getLong(durationColumn)
                        val path = cursor.getString(pathColumn)
                        val size = cursor.getLong(sizeColumn)
                        val dateAdded = cursor.getLong(dateAddedColumn)
                        
                        Log.d("AudioRepository", """
                            Fichier trouvé:
                            - Titre: $title
                            - Artiste: $artist
                            - Album: $album
                            - Chemin: $path
                            - Taille: $size
                            - Durée: $duration
                        """.trimIndent())
                        
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
                    } catch (e: Exception) {
                        Log.e("AudioRepository", "Erreur lors de la lecture d'un fichier: ${e.message}")
                        e.printStackTrace()
                    }
                }
            } ?: run {
                Log.e("AudioRepository", "La requête a retourné null")
            }
        } catch (e: Exception) {
            Log.e("AudioRepository", "Erreur lors de la requête: ${e.message}")
            e.printStackTrace()
        }
        
        Log.d("AudioRepository", "Nombre total de fichiers audio: ${audioList.size}")
        audioList
    }

    override suspend fun getAudioById(id: Long): Audio? = withContext(Dispatchers.IO) {
        // TODO: Implémenter
        null
    }

    override suspend fun getFavorites(): List<Audio> = withContext(Dispatchers.IO) {
        // TODO: Implémenter
        emptyList()
    }

    override suspend fun getAudiosByArtist(artist: String): List<Audio> = withContext(Dispatchers.IO) {
        // TODO: Implémenter
        emptyList()
    }

    override suspend fun getAudiosByAlbum(album: String): List<Audio> = withContext(Dispatchers.IO) {
        // TODO: Implémenter
        emptyList()
    }

    override suspend fun toggleFavorite(audio: Audio) {
        // TODO: Implémenter
    }

    override suspend fun updateAudioMetadata(audio: Audio) {
        // TODO: Implémenter
    }

    override suspend fun deleteAudio(audio: Audio) {
        withContext(Dispatchers.IO) {
            context.contentResolver.delete(audio.uri, null, null)
        }
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
                duration = duration,
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