package com.example.p_music.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p_music.domain.model.Video
import com.example.p_music.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VideosUiState(
    val videos: List<Video> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class VideosViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideosUiState())
    val uiState: StateFlow<VideosUiState> = _uiState.asStateFlow()

    init {
        loadVideos()
    }

    private fun loadVideos() {
        viewModelScope.launch {
            videoRepository.getAllVideos()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
                .collect { videos ->
                    _uiState.update { 
                        it.copy(
                            videos = videos,
                            isLoading = false
                        )
                    }
                }
        }
    }
} 