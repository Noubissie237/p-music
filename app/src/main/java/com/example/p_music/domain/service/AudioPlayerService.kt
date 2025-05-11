package com.example.p_music.domain.service

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.p_music.domain.model.Audio
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayerService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var player: ExoPlayer? = null
    private var currentAudio: Audio? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentProgress = MutableStateFlow(0f)
    val currentProgress: StateFlow<Float> = _currentProgress

    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            _isPlaying.value = isPlaying
                        }
                        Player.STATE_ENDED -> {
                            _isPlaying.value = false
                            _currentProgress.value = 0f
                        }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }
            })
        }
    }

    fun play(audio: Audio) {
        if (currentAudio?.id == audio.id) {
            player?.play()
            return
        }

        currentAudio = audio
        val mediaItem = MediaItem.fromUri(audio.uri)
        player?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    fun pause() {
        player?.pause()
    }

    fun resume() {
        player?.play()
    }

    fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    fun release() {
        player?.release()
        player = null
    }

    fun getCurrentAudio(): Audio? = currentAudio
} 