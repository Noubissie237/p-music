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
            audioRepository.getAudios().collect { audios ->
                _uiState.update { it.copy(audioList = audios) }
            }
        }
    }
} 