package com.example.p_music.di

import android.content.Context
import androidx.room.Room
import com.example.p_music.data.local.AppDatabase
import com.example.p_music.data.local.dao.FavoriteAudioDao
import com.example.p_music.data.local.dao.FavoriteVideoDao
import com.example.p_music.data.local.dao.PlaylistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "p_music_database"
        )
        .fallbackToDestructiveMigration() // Permet de recréer la DB si la version change
        .build()
    }

    @Provides
    @Singleton
    fun provideFavoriteAudioDao(database: AppDatabase): FavoriteAudioDao {
        return database.favoriteAudioDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteVideoDao(database: AppDatabase): FavoriteVideoDao {
        return database.favoriteVideoDao()
    }

    @Provides
    @Singleton
    fun providePlaylistDao(database: AppDatabase): PlaylistDao {
        return database.playlistDao()
    }
} 