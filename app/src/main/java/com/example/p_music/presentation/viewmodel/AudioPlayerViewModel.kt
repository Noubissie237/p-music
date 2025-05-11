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
    val error: String? = null
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
            combine(
                audioPlayerService.currentAudio,
                audioPlayerService.isPlaying,
                audioPlayerService.currentProgress,
                audioPlayerService.elapsedTime,
                audioPlayerService.remainingTime
            ) { audio, isPlaying, progress, elapsed, remaining ->
                _uiState.update { 
                    it.copy(
                        currentAudio = audio,
                        isPlaying = isPlaying,
                        progress = progress,
                        elapsedTime = elapsed,
                        remainingTime = remaining
                    )
                }
            }.collect()
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
        viewModelScope.launch {
            if (_uiState.value.currentAudio?.id != audioId) {
                _uiState.update { it.copy(isLoading = true) }
                try {
                    audioRepository.getAudioById(audioId)?.let { audio ->
                        audioPlayerService.playAudio(audio)
                    } ?: run {
                        _uiState.update { 
                            it.copy(
                                error = "Audio non trouvé",
                                isLoading = false
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.update { 
                        it.copy(
                            error = e.message ?: "Une erreur est survenue",
                            isLoading = false
                        )
                    }
                }
            }
        }
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
} 