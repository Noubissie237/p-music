package com.example.p_music.presentation.ui.screens

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.p_music.domain.model.Video
import com.example.p_music.presentation.viewmodel.VideoPlayerViewModel
import androidx.compose.foundation.shape.CircleShape
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    onNavigateBack: () -> Unit,
    videoId: String,
    viewModel: VideoPlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(videoId) {
        viewModel.loadVideo(videoId.toLong())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lecture vidéo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
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
                    val errorMessage = uiState.error
                    Text(
                        text = errorMessage!!,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Lecteur vidéo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            AndroidView(
                                factory = { context ->
                                    PlayerView(context).apply {
                                        layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                                        useController = true
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )

                            // Contrôles de lecture
                            if (!uiState.isFullscreen) {
                                VideoControls(
                                    video = uiState.currentVideo,
                                    isPlaying = uiState.isPlaying,
                                    onPlayPauseClick = { viewModel.togglePlayPause() },
                                    onNextClick = { viewModel.playNext() },
                                    onPreviousClick = { viewModel.playPrevious() },
                                    onFavoriteClick = { viewModel.toggleFavorite() },
                                    onFullscreenClick = { /* TODO: Implémenter le mode plein écran */ }
                                )
                            }
                        }

                        // Liste des vidéos
                        if (!uiState.isFullscreen) {
                            VideoList(
                                videos = uiState.videoList,
                                currentVideo = uiState.currentVideo,
                                onVideoClick = { viewModel.playVideo(it) }
                            )
                        }
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
        // Barre supérieure
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

        // Contrôles centraux
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

        // Informations de la vidéo
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

@Composable
private fun VideoList(
    videos: List<Video>,
    currentVideo: Video?,
    onVideoClick: (Video) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFF1A1A1A))
    ) {
        Text(
            text = "Vidéos",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )

        videos.forEach { video ->
            VideoItem(
                video = video,
                isPlaying = video.id == currentVideo?.id,
                onItemClick = { onVideoClick(video) }
            )
        }
    }
}

@Composable
private fun VideoItem(
    video: Video,
    isPlaying: Boolean,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Miniature
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color.Gray)
        ) {
            // TODO: Afficher la miniature de la vidéo
        }

        // Informations
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = video.title,
                style = MaterialTheme.typography.titleSmall,
                color = if (isPlaying) MaterialTheme.colorScheme.primary else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formatDuration(video.duration.toMillis()),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
    return String.format("%02d:%02d", minutes, seconds)
} 