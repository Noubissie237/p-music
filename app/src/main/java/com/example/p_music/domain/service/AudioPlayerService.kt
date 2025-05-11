package com.example.p_music.domain.service

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.p_music.domain.model.Audio
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayerService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exoPlayer: ExoPlayer
) {
    private var playlist: List<Audio> = emptyList()
    private var currentIndex = -1
    private var progressUpdateJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentProgress = MutableStateFlow(0f)
    val currentProgress: StateFlow<Float> = _currentProgress.asStateFlow()

    private val _currentAudio = MutableStateFlow<Audio?>(null)
    val currentAudio: StateFlow<Audio?> = _currentAudio.asStateFlow()

    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime.asStateFlow()

    private val _remainingTime = MutableStateFlow(0L)
    val remainingTime: StateFlow<Long> = _remainingTime.asStateFlow()

    init {
        setupExoPlayer()
    }

    private fun setupExoPlayer() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_READY -> {
                        _isPlaying.value = exoPlayer.isPlaying
                        if (exoPlayer.isPlaying) {
                            startProgressUpdates()
                        }
                    }
                    Player.STATE_ENDED -> {
                        playNext()
                    }
                }
            }
        })
    }

    private fun startProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                if (exoPlayer.isPlaying) {
                    val duration = exoPlayer.duration.toFloat()
                    if (duration > 0) {
                        val currentPosition = exoPlayer.currentPosition
                        _currentProgress.value = currentPosition.toFloat() / duration
                        _elapsedTime.value = currentPosition
                        _remainingTime.value = exoPlayer.duration - currentPosition
                    }
                }
                delay(1000)
            }
        }
    }

    fun setPlaylist(audios: List<Audio>, autoPlay: Boolean = true) {
        playlist = audios
        currentIndex = -1
        if (audios.isNotEmpty() && autoPlay) {
            currentIndex = 0
            prepareAndPlay(audios[0])
        }
    }

    fun playAudio(audio: Audio) {
        val index = playlist.indexOf(audio)
        if (index != -1) {
            currentIndex = index
            prepareAndPlay(audio)
        }
    }

    private fun prepareAndPlay(audio: Audio) {
        try {
            val mediaItem = MediaItem.fromUri(audio.uri)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
            _currentAudio.value = audio
            _isPlaying.value = true
            startProgressUpdates()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            _isPlaying.value = false
            progressUpdateJob?.cancel()
        } else {
            exoPlayer.play()
            _isPlaying.value = true
            startProgressUpdates()
        }
    }

    fun playNext() {
        if (playlist.isEmpty()) return
        
        currentIndex = (currentIndex + 1) % playlist.size
        prepareAndPlay(playlist[currentIndex])
    }

    fun playPrevious() {
        if (playlist.isEmpty()) return
        
        currentIndex = if (currentIndex > 0) currentIndex - 1 else playlist.size - 1
        prepareAndPlay(playlist[currentIndex])
    }

    fun seekTo(position: Float) {
        val duration = exoPlayer.duration
        if (duration > 0) {
            val newPosition = (position * duration).toLong()
            exoPlayer.seekTo(newPosition)
            _currentProgress.value = position
            _elapsedTime.value = newPosition
            _remainingTime.value = duration - newPosition
        }
    }

    fun release() {
        progressUpdateJob?.cancel()
        exoPlayer.release()
    }
} 