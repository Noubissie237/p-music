package com.example.p_music.di

import android.content.Context
import com.example.p_music.data.repository.AudioRepositoryImpl
import com.example.p_music.data.repository.FavoriteAudioRepositoryImpl
import com.example.p_music.data.repository.VideoRepositoryImpl
import com.example.p_music.domain.repository.AudioRepository
import com.example.p_music.domain.repository.FavoriteAudioRepository
import com.example.p_music.domain.repository.VideoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAudioRepository(
        audioRepositoryImpl: AudioRepositoryImpl
    ): AudioRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteAudioRepository(
        favoriteAudioRepositoryImpl: FavoriteAudioRepositoryImpl
    ): FavoriteAudioRepository

    @Binds
    @Singleton
    abstract fun bindVideoRepository(
        videoRepositoryImpl: VideoRepositoryImpl
    ): VideoRepository

    companion object {
        @Provides
        @Singleton
        fun provideAudioRepositoryImpl(
            @ApplicationContext context: Context
        ): AudioRepositoryImpl {
            return AudioRepositoryImpl(context)
        }

        @Provides
        @Singleton
        fun provideFavoriteAudioRepositoryImpl(
            @ApplicationContext context: Context
        ): FavoriteAudioRepositoryImpl {
            return FavoriteAudioRepositoryImpl(context)
        }
    }
} 