package com.example.p_music.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.p_music.presentation.ui.theme.SpotifyColors
import java.util.concurrent.TimeUnit

@Composable
fun MiniPlayer(
    title: String,
    artist: String,
    isPlaying: Boolean,
    progress: Float,
    elapsedTime: Long,
    remainingTime: Long,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onPlayerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        color = SpotifyColors.DarkGray,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onPlayerClick)
        ) {
            // Barre de progression
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = SpotifyColors.Green,
                trackColor = SpotifyColors.MediumGray
            )

            // Contenu du player
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Informations de la chanson
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = SpotifyColors.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = artist,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SpotifyColors.LightGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Contrôles de lecture
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPreviousClick) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Précédent",
                            tint = SpotifyColors.White
                        )
                    }

                    IconButton(
                        onClick = onPlayPauseClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Lecture",
                            tint = SpotifyColors.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(onClick = onNextClick) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Suivant",
                            tint = SpotifyColors.White
                        )
                    }
                }
            }

            // Temps écoulé et restant
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(elapsedTime),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = formatDuration(remainingTime),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
    return String.format("%02d:%02d", minutes, seconds)
} 