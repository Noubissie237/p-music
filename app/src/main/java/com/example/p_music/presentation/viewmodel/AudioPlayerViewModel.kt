package com.example.p_music.presentation.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.repository.AudioRepository
import com.example.p_music.domain.usecase.favorite.GetFavoritesUseCase
import com.example.p_music.domain.usecase.favorite.ToggleFavoriteUseCase
import com.example.p_music.domain.usecase.GetAudiosUseCase
import android.media.MediaPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    application: Application,
    private val repository: AudioRepository,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getAudiosUseCase: GetAudiosUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AudioPlayerUiState())
    val uiState: StateFlow<AudioPlayerUiState> = _uiState.asStateFlow()

    private var exoPlayer: ExoPlayer? = null
    private var currentAudioList: List<Audio> = emptyList()
    private var currentAudioIndex: Int = -1
    private var mediaPlayer: MediaPlayer? = null
    private var currentAudio: Audio? = null
    private var isPlaying = false

    init {
        initializeExoPlayer()
        loadAudios()
        observeFavorites()
    }

    private fun initializeExoPlayer() {
        exoPlayer = ExoPlayer.Builder(getApplication()).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_READY -> {
                            _uiState.update { it.copy(isPlaying = isPlaying) }
                        }
                        Player.STATE_ENDED -> {
                            playNext()
                        }
                    }
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    _uiState.update { it.copy(isPlaying = playing) }
                }
            })
        }
    }

    private fun loadAudios() {
        getAudiosUseCase()
            .onEach { audios ->
                currentAudioList = audios
                _uiState.update { it.copy(audioList = audios) }
            }
            .catch { error ->
                _uiState.update { it.copy(error = error.message ?: "Une erreur est survenue") }
            }
            .launchIn(viewModelScope)
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            getFavoritesUseCase().collect { favorites ->
                _uiState.update { currentState ->
                    val updatedList = currentState.audioList.map { audio ->
                        audio.copy(isFavorite = favorites.any { it.id == audio.id })
                    }
                    currentState.copy(
                        audioList = updatedList,
                        currentAudio = currentState.currentAudio?.let { current ->
                            favorites.find { it.id == current.id }?.let { favorite ->
                                current.copy(isFavorite = true)
                            } ?: current
                        }
                    )
                }
            }
        }
    }

    fun playAudio(audio: Audio) {
        val index = currentAudioList.indexOf(audio)
        if (index != -1) {
            currentAudioIndex = index
            exoPlayer?.apply {
                setMediaItem(MediaItem.fromUri(audio.uri))
                prepare()
                play()
            }
            _uiState.update { 
                it.copy(
                    currentAudio = audio,
                    isPlaying = true
                )
            }
        }
    }

    fun togglePlayPause() {
        exoPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
            _uiState.update { it.copy(isPlaying = player.isPlaying) }
        }
    }

    fun playNext() {
        if (currentAudioList.isNotEmpty()) {
            currentAudioIndex = (currentAudioIndex + 1) % currentAudioList.size
            playAudio(currentAudioList[currentAudioIndex])
        }
    }

    fun playPrevious() {
        if (currentAudioList.isNotEmpty()) {
            currentAudioIndex = if (currentAudioIndex > 0) currentAudioIndex - 1 else currentAudioList.size - 1
            playAudio(currentAudioList[currentAudioIndex])
        }
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _uiState.value.currentAudio?.let { audio ->
                toggleFavoriteUseCase(audio)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
        exoPlayer = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

data class AudioPlayerUiState(
    val audioList: List<Audio> = emptyList(),
    val currentAudio: Audio? = null,
    val isPlaying: Boolean = false,
    val error: String? = null
) 