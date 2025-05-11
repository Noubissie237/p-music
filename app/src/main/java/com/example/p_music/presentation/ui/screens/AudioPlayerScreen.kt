package com.example.p_music.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.p_music.R
import com.example.p_music.domain.model.Audio
import com.example.p_music.presentation.viewmodel.AudioPlayerViewModel
import com.example.p_music.presentation.viewmodel.AudioPlayerUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerScreen(
    viewModel: AudioPlayerViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }
            uiState.error != null -> {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }
            uiState.currentAudio != null -> {
                AudioContent(
                    uiState = uiState,
                    onPlayPauseClick = { viewModel.togglePlayPause() },
                    onSeek = { viewModel.seekTo(it) },
                    onFavoriteClick = { viewModel.toggleFavorite() }
                )
            }
        }
    }
}

@Composable
private fun AudioContent(
    uiState: AudioPlayerUiState,
    onPlayPauseClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onFavoriteClick: () -> Unit
) {
    val audio = uiState.currentAudio!!

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pochette de l'album
        audio.coverUri?.let { uri ->
            Image(
                painter = painterResource(id = R.drawable.ic_music_note),
                contentDescription = "Pochette de l'album",
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Informations sur la musique
        Text(
            text = audio.title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = audio.artist,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Contrôles de lecture
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (audio.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favoris"
                )
            }

            IconButton(onClick = onPlayPauseClick) {
                Icon(
                    imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (uiState.isPlaying) "Pause" else "Lecture"
                )
            }
        }

        // Barre de progression
        Slider(
            value = uiState.currentPosition.toFloat(),
            onValueChange = { onSeek(it.toLong()) },
            valueRange = 0f..uiState.duration.toFloat(),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
} 