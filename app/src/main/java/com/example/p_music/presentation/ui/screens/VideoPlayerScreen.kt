package com.example.p_music.presentation.ui.screens

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView
import com.example.p_music.domain.model.Video
import com.example.p_music.presentation.viewmodel.VideoPlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    onNavigateBack: () -> Unit,
    videoId: String,
    viewModel: VideoPlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var playerView by remember { mutableStateOf<PlayerView?>(null) }

    LaunchedEffect(videoId) {
        viewModel.loadVideo(videoId.toLong())
    }

    LaunchedEffect(uiState.currentVideo) {
        uiState.currentVideo?.let { video ->
            viewModel.playVideo(video)
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AndroidView(
                            factory = { context ->
                                PlayerView(context).apply {
                                    layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                                    useController = true
                                    player = viewModel.getExoPlayer()
                                    playerView = this
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        VideoControls(
                            video = uiState.currentVideo,
                            isPlaying = uiState.isPlaying,
                            onPlayPauseClick = { viewModel.togglePlayPause() },
                            onNextClick = { viewModel.playNext() },
                            onPreviousClick = { viewModel.playPrevious() },
                            onFavoriteClick = { viewModel.toggleFavorite() },
                            onFullscreenClick = { /* À faire */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoControls(
    video: Video?,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onFullscreenClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onFullscreenClick) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Plein écran",
                    tint = Color.White
                )
            }

            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (video?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (video?.isFavorite == true) "Retirer des favoris" else "Ajouter aux favoris",
                    tint = if (video?.isFavorite == true) MaterialTheme.colorScheme.primary else Color.White
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousClick) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Précédent",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Lecture",
                    tint = Color.White
                )
            }

            IconButton(onClick = onNextClick) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Suivant",
                    tint = Color.White
                )
            }
        }

        video?.let {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = it.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = it.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}