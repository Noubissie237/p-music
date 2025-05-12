package com.example.p_music.presentation.ui.screens

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView
import com.example.p_music.domain.model.Video
import com.example.p_music.presentation.viewmodel.VideoPlayerViewModel
import kotlinx.coroutines.delay

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
    var controlsVisible by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(0f) }
    var currentTime by remember { mutableStateOf("00:00") }
    var durationTime by remember { mutableStateOf("00:00") }
    var showSettings by remember { mutableStateOf(false) }

    LaunchedEffect(videoId) {
        viewModel.loadVideo(videoId.toLong())
    }

    LaunchedEffect(uiState.currentVideo) {
        uiState.currentVideo?.let { video ->
            viewModel.playVideo(video)
        }
    }

    LaunchedEffect(controlsVisible) {
        if (controlsVisible) {
            delay(3000)
            controlsVisible = false
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            viewModel.getExoPlayer()?.let { player ->
                if (player.duration > 0) {
                    val currentPosition = player.currentPosition
                    val totalDuration = player.duration
                    progress = currentPosition / totalDuration.toFloat()
                    currentTime = formatTime(currentPosition)
                    durationTime = formatTime(totalDuration)
                }
            }
            delay(500)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { controlsVisible = true }
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
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
            else -> {
                AndroidView(
                    factory = { context ->
                        PlayerView(context).apply {
                            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                            useController = false
                            player = viewModel.getExoPlayer()
                            playerView = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                AnimatedVisibility(
                    visible = controlsVisible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    VideoControlsOverlay(
                        video = uiState.currentVideo,
                        isPlaying = uiState.isPlaying,
                        progress = progress,
                        currentTime = currentTime,
                        durationTime = durationTime,
                        onProgressChange = { ratio ->
                            viewModel.getExoPlayer()?.seekTo((ratio * (viewModel.getExoPlayer()?.duration ?: 0L)).toLong())
                        },
                        onPlayPauseClick = { viewModel.togglePlayPause() },
                        onNextClick = { viewModel.playNext() },
                        onPreviousClick = { viewModel.playPrevious() },
                        onFavoriteClick = { viewModel.toggleFavorite() },
                        onBackClick = onNavigateBack,
                        onSettingsClick = { showSettings = true }
                    )
                }
            }
        }
    }

    if (showSettings) {
        VideoSettingsBottomSheet(
            viewModel = viewModel,
            onDismiss = { showSettings = false }
        )
    }
}

@Composable
private fun VideoControlsOverlay(
    video: Video?,
    isPlaying: Boolean,
    progress: Float,
    currentTime: String,
    durationTime: String,
    onProgressChange: (Float) -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Retour",
                    tint = Color.White
                )
            }

            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Paramètres",
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

        Column {
            Slider(
                value = progress,
                onValueChange = onProgressChange,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = currentTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
                Text(
                    text = durationTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
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
                        .size(64.dp)
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
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = it.title,
                        style = MaterialTheme.typography.titleLarge,
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
}

private fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
