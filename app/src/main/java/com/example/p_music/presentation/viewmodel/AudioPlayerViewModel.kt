package com.example.p_music.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.repository.AudioRepository
import com.example.p_music.domain.repository.FavoriteAudioRepository
import com.example.p_music.domain.usecase.favorite.GetFavoritesUseCase
import com.example.p_music.domain.usecase.favorite.ToggleFavoriteUseCase
import com.example.p_music.domain.usecase.GetAudiosUseCase
import android.media.MediaPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class AudioPlayerUiState(
    val currentAudio: Audio? = null,
    val isPlaying: Boolean = false,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val error: String? = null,
    val audioList: List<Audio> = emptyList()
)

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioRepository: AudioRepository,
    private val favoriteAudioRepository: FavoriteAudioRepository,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getAudiosUseCase: GetAudiosUseCase
) : ViewModel() {

    private var player: ExoPlayer? = null
    private val _uiState = MutableStateFlow(AudioPlayerUiState())
    val uiState: StateFlow<AudioPlayerUiState> = _uiState.asStateFlow()

    private var currentAudioList: List<Audio> = emptyList()
    private var currentAudioIndex: Int = -1
    private var mediaPlayer: MediaPlayer? = null
    private var currentAudio: Audio? = null
    private var isPlaying = false

    init {
        initializePlayer()
        loadAudioList()
        observeFavorites()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            _uiState.update { it.copy(
                                isPlaying = isPlaying,
                                duration = duration,
                                error = null
                            ) }
                        }
                        Player.STATE_ENDED -> {
                            _uiState.update { it.copy(isPlaying = false) }
                        }
                        Player.STATE_BUFFERING -> {
                            // Gérer le buffering si nécessaire
                        }
                        Player.STATE_IDLE -> {
                            // Gérer l'état idle si nécessaire
                        }
                    }
                }
            })
        }
    }

    private fun loadAudioList() {
        viewModelScope.launch {
            audioRepository.getAudios().collect { audios ->
                currentAudioList = audios
                _uiState.update { it.copy(audioList = audios) }
            }
        }
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

    fun loadAudio(audioId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val audio = audioRepository.getAudioById(audioId)
                if (audio != null) {
                    _uiState.update { it.copy(
                        currentAudio = audio,
                        duration = audio.duration,
                        isLoading = false
                    ) }
                    player?.setMediaItem(MediaItem.fromUri(audio.uri))
                    player?.prepare()
                    checkFavoriteStatus(audio.id)
                } else {
                    _uiState.update { it.copy(
                        error = "Audio non trouvé",
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = e.message ?: "Erreur inconnue",
                    isLoading = false
                ) }
            }
        }
    }

    private fun checkFavoriteStatus(audioId: String) {
        viewModelScope.launch {
            val isFavorite = favoriteAudioRepository.isFavorite(audioId)
            _uiState.update { it.copy(isFavorite = isFavorite) }
        }
    }

    fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
                _uiState.update { state -> state.copy(isPlaying = false) }
            } else {
                it.play()
                _uiState.update { state -> state.copy(isPlaying = true) }
            }
        }
    }

    fun seekTo(position: Long) {
        player?.seekTo(position)
        _uiState.update { it.copy(currentPosition = position) }
    }

    fun toggleFavorite() {
        _uiState.value.currentAudio?.let { audio ->
            viewModelScope.launch {
                favoriteAudioRepository.toggleFavorite(audio)
                checkFavoriteStatus(audio.id)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player?.release()
        player = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
} 