package com.example.p_music.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.repository.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MusicUiState(
    val audioList: List<Audio> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val audioRepository: AudioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicUiState())
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    init {
        loadAudios()
    }

    private fun loadAudios() {
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
                val filteredAudios = audioRepository.getAudios().collect { audios ->
                    val filtered = audios.filter { audio ->
                        audio.title.contains(query, ignoreCase = true) ||
                        audio.artist.contains(query, ignoreCase = true) ||
                        audio.album.contains(query, ignoreCase = true)
                    }
                    _uiState.update { 
                        it.copy(
                            audioList = filtered,
                            isLoading = false,
                            error = null
                        )
                    }
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
} 