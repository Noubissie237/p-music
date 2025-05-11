package com.example.p_music.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.repository.FavoriteAudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val favorites: List<Audio> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteAudioRepository: FavoriteAudioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            favoriteAudioRepository.getAllFavorites()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Une erreur est survenue"
                        )
                    }
                }
                .collect { favorites ->
                    _uiState.update { 
                        it.copy(
                            favorites = favorites,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun toggleFavorite(audio: Audio) {
        viewModelScope.launch {
            favoriteAudioRepository.toggleFavorite(audio)
        }
    }
} 