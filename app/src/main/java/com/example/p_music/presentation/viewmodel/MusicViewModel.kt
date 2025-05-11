package com.example.p_music.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.repository.AudioRepository
import com.example.p_music.domain.service.AudioPlayerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MusicUiState(
    val audioList: List<Audio> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentAudio: Audio? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val elapsedTime: Long = 0L,
    val remainingTime: Long = 0L
)

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val audioPlayerService: AudioPlayerService
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicUiState())
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    init {
        loadAudios()
        observePlayerState()
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

    fun loadAudios() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                audioRepository.getAudios().collect { audios ->
                    _uiState.update { 
                        it.copy(
                            audioList = audios,
                            isLoading = false,
                            error = null
                        )
                    }
                    // Mettre à jour la playlist du service sans lancer la lecture
                    audioPlayerService.setPlaylist(audios, false)
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Une erreur est survenue"
                    )
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun search() {
        val query = _uiState.value.searchQuery
        if (query.isBlank()) {
            loadAudios()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                audioRepository.getAudios().collect { audios ->
                    val filtered = audios.filter { audio ->
                        audio.title.contains(query, ignoreCase = true) ||
                        audio.artist.contains(query, ignoreCase = true)
                    }
                    _uiState.update { 
                        it.copy(
                            audioList = filtered,
                            isLoading = false,
                            error = null
                        )
                    }
                    // Mettre à jour la playlist du service sans lancer la lecture
                    audioPlayerService.setPlaylist(filtered, false)
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Une erreur est survenue"
                    )
                }
            }
        }
    }

    fun playAudio(audio: Audio) {
        audioPlayerService.playAudio(audio)
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

    override fun onCleared() {
        super.onCleared()
        // Le service sera détruit automatiquement par le système
        // quand il n'y aura plus de clients connectés
    }
} 