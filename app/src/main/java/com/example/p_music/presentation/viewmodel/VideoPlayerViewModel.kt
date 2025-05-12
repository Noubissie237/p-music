package com.example.p_music.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.p_music.domain.model.Video
import com.example.p_music.domain.repository.VideoRepository
import com.example.p_music.domain.usecase.video.GetVideosUseCase
import com.example.p_music.domain.usecase.video.ToggleVideoFavoriteUseCase
import com.example.p_music.presentation.ui.screens.extractThumbnail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.mutableStateMapOf


data class VideoPlayerUiState(
    val currentVideo: Video? = null,
    val videoList: List<Video> = emptyList(),
    val isPlaying: Boolean = false,
    val isFullscreen: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    application: Application,
    private val repository: VideoRepository,
    private val getVideosUseCase: GetVideosUseCase,
    private val toggleFavoriteUseCase: ToggleVideoFavoriteUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(VideoPlayerUiState())
    val uiState: StateFlow<VideoPlayerUiState> = _uiState.asStateFlow()

    // Cache de thumbnails
    private val _thumbnailCache = mutableStateMapOf<Long, Bitmap?>()
    val thumbnailCache: Map<Long, Bitmap?> = _thumbnailCache

    private var exoPlayer: ExoPlayer? = null
    private var currentVideoList: List<Video> = emptyList()
    private var currentVideoIndex: Int = -1

    init {
        initializeExoPlayer()
        loadVideos()
        observeFavorites()
    }

    // Précharge la miniature si absente
    fun loadThumbnailIfNeeded(video: Video, context: Context) {
        if (_thumbnailCache.containsKey(video.id)) return
        viewModelScope.launch(Dispatchers.IO) {
            val bitmap = extractThumbnail(context, video.uri)
            _thumbnailCache[video.id] = bitmap
        }
    }

    private fun initializeExoPlayer() {
        exoPlayer = ExoPlayer.Builder(getApplication()).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) playNext()
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    _uiState.update { it.copy(isPlaying = playing) }
                }
            })
        }
    }

    private fun loadVideos() {
        viewModelScope.launch {
            getVideosUseCase().collect { videos ->
                currentVideoList = videos
                _uiState.update { it.copy(videoList = videos) }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getAllFavorites().collect { favorites ->
                _uiState.update { currentState ->
                    val updatedList = currentState.videoList.map { video ->
                        video.copy(isFavorite = favorites.any { it.id == video.id })
                    }
                    currentState.copy(
                        videoList = updatedList,
                        currentVideo = currentState.currentVideo?.let { current ->
                            favorites.find { it.id == current.id }?.let {
                                current.copy(isFavorite = true)
                            } ?: current
                        }
                    )
                }
            }
        }
    }

    fun loadVideo(videoId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val video = repository.getVideoById(videoId)
                getVideosUseCase().collect { videos ->
                    _uiState.update {
                        it.copy(
                            currentVideo = video,
                            videoList = videos,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Erreur lors du chargement de la vidéo"
                    )
                }
            }
        }
    }

    fun getExoPlayer(): ExoPlayer? = exoPlayer

    fun playVideo(video: Video) {
        val index = currentVideoList.indexOf(video)
        if (index != -1) {
            currentVideoIndex = index
            exoPlayer?.apply {
                setMediaItem(MediaItem.fromUri(video.uri))
                prepare()
                play()
            }
            _uiState.update {
                it.copy(currentVideo = video, isPlaying = true)
            }
        }
    }

    fun togglePlayPause() {
        exoPlayer?.let { player ->
            if (player.isPlaying) player.pause() else player.play()
            _uiState.update { it.copy(isPlaying = player.isPlaying) }
        }
    }

    fun playNext() {
        if (currentVideoList.isNotEmpty()) {
            currentVideoIndex = (currentVideoIndex + 1) % currentVideoList.size
            playVideo(currentVideoList[currentVideoIndex])
        }
    }

    fun playPrevious() {
        if (currentVideoList.isNotEmpty()) {
            currentVideoIndex = if (currentVideoIndex > 0) currentVideoIndex - 1 else currentVideoList.size - 1
            playVideo(currentVideoList[currentVideoIndex])
        }
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _uiState.value.currentVideo?.let { toggleFavoriteUseCase(it) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
        exoPlayer = null
    }
}
