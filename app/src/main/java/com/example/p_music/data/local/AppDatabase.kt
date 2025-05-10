package com.example.p_music.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.p_music.data.local.dao.FavoriteAudioDao
import com.example.p_music.data.local.dao.FavoriteVideoDao
import com.example.p_music.data.local.entity.FavoriteAudioEntity
import com.example.p_music.data.local.entity.FavoriteVideoEntity

@Database(
    entities = [
        FavoriteAudioEntity::class,
        FavoriteVideoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteAudioDao(): FavoriteAudioDao
    abstract fun favoriteVideoDao(): FavoriteVideoDao
} 