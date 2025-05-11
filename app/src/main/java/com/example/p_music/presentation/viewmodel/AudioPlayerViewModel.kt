package com.example.p_music.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.repository.AudioRepository
import com.example.p_music.domain.repository.FavoriteAudioRepository
import com.example.p_music.domain.service.AudioPlayerService
import com.example.p_music.domain.usecase.favorite.GetFavoritesUseCase
import com.example.p_music.domain.usecase.favorite.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AudioPlayerUiState(
    val currentAudio: Audio? = null,
    val isPlaying: Boolean = false,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val progress: Float = 0f,
    val elapsedTime: Long = 0L,
    val remainingTime: Long = 0L,
    val error: String? = null,
    val isRepeatMode: Boolean = false,
    val isShuffleMode: Boolean = false
)

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val favoriteAudioRepository: FavoriteAudioRepository,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val audioPlayerService: AudioPlayerService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AudioPlayerUiState())
    val uiState: StateFlow<AudioPlayerUiState> = _uiState.asStateFlow()

    init {
        observePlayerState()
        observeFavorites()
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            audioPlayerService.currentAudio.collect { audio ->
                _uiState.update { it.copy(currentAudio = audio) }
            }
        }
        viewModelScope.launch {
            audioPlayerService.isPlaying.collect { isPlaying ->
                _uiState.update { it.copy(isPlaying = isPlaying) }
            }
        }
        viewModelScope.launch {
            audioPlayerService.currentProgress.collect { progress ->
                _uiState.update { it.copy(progress = progress) }
            }
        }
        viewModelScope.launch {
            audioPlayerService.elapsedTime.collect { elapsed ->
                _uiState.update { it.copy(elapsedTime = elapsed) }
            }
        }
        viewModelScope.launch {
            audioPlayerService.remainingTime.collect { remaining ->
                _uiState.update { it.copy(remainingTime = remaining) }
            }
        }
        viewModelScope.launch {
            audioPlayerService.isRepeatMode.collect { isRepeat ->
                _uiState.update { it.copy(isRepeatMode = isRepeat) }
            }
        }
        viewModelScope.launch {
            audioPlayerService.isShuffleMode.collect { isShuffle ->
                _uiState.update { it.copy(isShuffleMode = isShuffle) }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            getFavoritesUseCase().collect { favorites ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isFavorite = currentState.currentAudio?.let { audio ->
                            favorites.any { it.id == audio.id }
                        } ?: false
                    )
                }
            }
        }
    }

    fun loadAudio(audioId: String) {
        // Cette fonction n'est plus nécessaire car l'audio est déjà chargé
        // dans le service via le MusicViewModel
    }

    fun togglePlayPause() {
        audioPlayerService.togglePlayPause()
    }

    fun playNext() {
        audioPlayerService.playNext()
    }

    fun playPrevious() {
        audioPlayerService.playPrevious()
    }

    fun seekTo(position: Float) {
        audioPlayerService.seekTo(position)
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _uiState.value.currentAudio?.let { audio ->
                toggleFavoriteUseCase(audio)
            }
        }
    }

    fun toggleRepeatMode() {
        audioPlayerService.toggleRepeatMode()
    }

    fun toggleShuffleMode() {
        audioPlayerService.toggleShuffleMode()
    }
} 