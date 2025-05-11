package com.example.p_music.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.repository.FavoriteAudioRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteAudioRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION), FavoriteAudioRepository {

    companion object {
        private const val DATABASE_NAME = "favorites.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "favorites"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_ARTIST = "artist"
        private const val COLUMN_ALBUM = "album"
        private const val COLUMN_DURATION = "duration"
        private const val COLUMN_PATH = "path"
        private const val COLUMN_URI = "uri"
        private const val COLUMN_COVER_URI = "cover_uri"
        private const val COLUMN_SIZE = "size"
        private const val COLUMN_DATE_ADDED = "date_added"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_ARTIST TEXT NOT NULL,
                $COLUMN_ALBUM TEXT NOT NULL,
                $COLUMN_DURATION INTEGER NOT NULL,
                $COLUMN_PATH TEXT NOT NULL,
                $COLUMN_URI TEXT NOT NULL,
                $COLUMN_COVER_URI TEXT,
                $COLUMN_SIZE INTEGER NOT NULL,
                $COLUMN_DATE_ADDED INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    override fun getAllFavorites(): Flow<List<Audio>> = flow {
        val favorites = mutableListOf<Audio>()
        val db = this@FavoriteAudioRepositoryImpl.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val audio = Audio(
                    id = getString(getColumnIndexOrThrow(COLUMN_ID)),
                    title = getString(getColumnIndexOrThrow(COLUMN_TITLE)),
                    artist = getString(getColumnIndexOrThrow(COLUMN_ARTIST)),
                    album = getString(getColumnIndexOrThrow(COLUMN_ALBUM)),
                    duration = getLong(getColumnIndexOrThrow(COLUMN_DURATION)),
                    path = getString(getColumnIndexOrThrow(COLUMN_PATH)),
                    uri = android.net.Uri.parse(getString(getColumnIndexOrThrow(COLUMN_URI))),
                    coverUri = getString(getColumnIndexOrThrow(COLUMN_COVER_URI))?.let { android.net.Uri.parse(it) },
                    size = getLong(getColumnIndexOrThrow(COLUMN_SIZE)),
                    dateAdded = getLong(getColumnIndexOrThrow(COLUMN_DATE_ADDED)),
                    isFavorite = true
                )
                favorites.add(audio)
            }
        }
        cursor.close()
        db.close()
        emit(favorites)
    }.flowOn(Dispatchers.IO)

    override suspend fun toggleFavorite(audio: Audio) {
        val db = this.writableDatabase
        if (isFavorite(audio.id)) {
            db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(audio.id))
        } else {
            val values = ContentValues().apply {
                put(COLUMN_ID, audio.id)
                put(COLUMN_TITLE, audio.title)
                put(COLUMN_ARTIST, audio.artist)
                put(COLUMN_ALBUM, audio.album)
                put(COLUMN_DURATION, audio.duration)
                put(COLUMN_PATH, audio.path)
                put(COLUMN_URI, audio.uri.toString())
                put(COLUMN_COVER_URI, audio.coverUri?.toString())
                put(COLUMN_SIZE, audio.size)
                put(COLUMN_DATE_ADDED, audio.dateAdded)
            }
            db.insert(TABLE_NAME, null, values)
        }
        db.close()
    }

    override suspend fun isFavorite(audioId: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID),
            "$COLUMN_ID = ?",
            arrayOf(audioId),
            null,
            null,
            null
        )
        val isFavorite = cursor.count > 0
        cursor.close()
        db.close()
        return isFavorite
    }
} 