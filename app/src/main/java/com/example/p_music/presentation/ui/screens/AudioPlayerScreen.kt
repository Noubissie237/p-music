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
import com.example.p_music.presentation.ui.components.*
import com.example.p_music.presentation.viewmodel.AudioPlayerViewModel
import com.example.p_music.presentation.viewmodel.AudioPlayerUiState
import java.util.concurrent.TimeUnit

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
        Box(
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp)
        ) {
            audio.coverUri?.let { uri ->
                Image(
                    painter = painterResource(id = R.drawable.ic_music_note),
                    contentDescription = "Pochette de l'album",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Informations sur la musique
        Text(
            text = audio.title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = audio.artist,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Barre de progression
        SpotifyProgressBar(
            progress = uiState.currentPosition.toFloat(),
            onProgressChange = { onSeek(it.toLong()) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        // Temps de lecture
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(uiState.currentPosition),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = formatDuration(uiState.duration),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Contrôles de lecture
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpotifyIconButton(
                icon = Icons.Default.Shuffle,
                onClick = { /* TODO: Implémenter la lecture aléatoire */ }
            )

            SpotifyIconButton(
                icon = Icons.Default.SkipPrevious,
                onClick = { /* TODO: Implémenter la piste précédente */ }
            )

            SpotifyPlayButton(
                isPlaying = uiState.isPlaying,
                onClick = onPlayPauseClick
            )

            SpotifyIconButton(
                icon = Icons.Default.SkipNext,
                onClick = { /* TODO: Implémenter la piste suivante */ }
            )

            SpotifyIconButton(
                icon = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                onClick = onFavoriteClick,
                tint = if (uiState.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
    return String.format("%02d:%02d", minutes, seconds)
} 