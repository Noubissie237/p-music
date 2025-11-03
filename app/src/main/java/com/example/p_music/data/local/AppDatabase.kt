package com.example.p_music.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.p_music.data.local.dao.FavoriteAudioDao
import com.example.p_music.data.local.dao.FavoriteVideoDao
import com.example.p_music.data.local.dao.PlaylistDao
import com.example.p_music.data.local.entity.FavoriteAudioEntity
import com.example.p_music.data.local.entity.FavoriteVideoEntity
import com.example.p_music.data.local.entity.PlaylistEntity
import com.example.p_music.data.local.entity.PlaylistAudioCrossRef

@Database(
    entities = [
        FavoriteAudioEntity::class,
        FavoriteVideoEntity::class,
        PlaylistEntity::class,
        PlaylistAudioCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteAudioDao(): FavoriteAudioDao
    abstract fun favoriteVideoDao(): FavoriteVideoDao
    abstract fun playlistDao(): PlaylistDao
} 