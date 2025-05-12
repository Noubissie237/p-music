package com.example.p_music.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p_music.domain.model.Video
import com.example.p_music.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    // ✅ Cache des miniatures vidéos : vidéo ID -> Bitmap (nullable si échec)
    private val _thumbnailCache = mutableStateMapOf<Long, Bitmap?>()
    val thumbnailCache: Map<Long, Bitmap?> get() = _thumbnailCache

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

    /**
     * ✅ Charge la miniature d'une vidéo, seulement si non déjà présente dans le cache.
     * Appelée par le composable au premier affichage.
     */
    fun loadThumbnail(video: Video, context: Context) {
        if (_thumbnailCache.containsKey(video.id)) return

        viewModelScope.launch(Dispatchers.IO) {
            val bitmap = extractThumbnail(context, video.uri)
            _thumbnailCache[video.id] = bitmap
        }
    }

    /**
     * ✅ Extraction robuste d'une miniature à 1 seconde de la vidéo.
     */
    private fun extractThumbnail(context: Context, videoUri: Uri): Bitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            context.contentResolver.openFileDescriptor(videoUri, "r")?.use { pfd ->
                retriever.setDataSource(pfd.fileDescriptor)
                val bitmap = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                retriever.release()
                bitmap
            }
        } catch (e: Exception) {
            Log.e("VideoThumbnail", "Extraction failed: ${e.message}")
            null
        }
    }
}
