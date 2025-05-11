package com.example.p_music.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.example.p_music.domain.service.AudioPlayerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }

    @Provides
    @Singleton
    fun provideAudioPlayerService(
        @ApplicationContext context: Context,
        exoPlayer: ExoPlayer
    ): AudioPlayerService {
        return AudioPlayerService(context, exoPlayer)
    }
} 