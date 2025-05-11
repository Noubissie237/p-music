package com.example.p_music.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p_music.presentation.ui.theme.SpotifyColors
import java.util.concurrent.TimeUnit

/**
 * Composant MiniPlayer de l'application P-Music
 * 
 * Affiche un contrôleur média compact au bas de l'écran avec:
 * - Informations de la piste en cours (titre/artiste)
 * - Contrôles de lecture (précédent, lecture/pause, suivant)
 * - Barre de progression avec temps écoulé/restant
 */
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
    onProgressChange: (Float) -> Unit,
    coverArtUrl: String? = null,
    modifier: Modifier = Modifier
) {
    // Animation de la progression
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "ProgressAnimation"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SpotifyColors.DarkGray
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onPlayerClick)
        ) {
            // Contenu principal du player
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Option: Ajouter la couverture de l'album si disponible
                if (coverArtUrl != null) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(SpotifyColors.MediumGray)
                    ) {
                        // Remplacer par AsyncImage avec coil pour charger l'image
                        // AsyncImage(
                        //     model = coverArtUrl,
                        //     contentDescription = "Couverture de $title",
                        //     contentScale = ContentScale.Crop,
                        //     modifier = Modifier.fillMaxSize()
                        // )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Informations de la piste
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = SpotifyColors.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = artist,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        ),
                        color = SpotifyColors.LightGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Contrôles de lecture
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPreviousClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SkipPrevious,
                            contentDescription = "Précédent",
                            tint = SpotifyColors.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    FilledIconButton(
                        onClick = onPlayPauseClick,
                        modifier = Modifier.size(44.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = SpotifyColors.Green,
                            contentColor = SpotifyColors.DarkGray
                        )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Lecture",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = onNextClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SkipNext,
                            contentDescription = "Suivant",
                            tint = SpotifyColors.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Barre de progression avec temps
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                // Barre de progression
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SpotifyColors.MediumGray.copy(alpha = 0.5f))
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val progress = (offset.x / size.width).coerceIn(0f, 1f)
                                onProgressChange(progress)
                            }
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(SpotifyColors.Green)
                    )
                }
                
                // Temps écoulé et restant
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDuration(elapsedTime),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            color = SpotifyColors.LightGray
                        )
                    )
                    Text(
                        text = "-" + formatDuration(remainingTime),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            color = SpotifyColors.LightGray
                        )
                    )
                }
            }
        }
    }
}

/**
 * Convertit une durée en millisecondes en format "mm:ss"
 */
private fun formatDuration(durationMs: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun MiniPlayerPreview() {
    Surface(
        color = Color(0xFF121212),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom
        ) {
            MiniPlayer(
                title = "Bohemian Rhapsody",
                artist = "Queen",
                isPlaying = true,
                progress = 0.7f,
                elapsedTime = TimeUnit.MINUTES.toMillis(3) + TimeUnit.SECONDS.toMillis(45),
                remainingTime = TimeUnit.MINUTES.toMillis(1) + TimeUnit.SECONDS.toMillis(30),
                onPlayPauseClick = {},
                onNextClick = {},
                onPreviousClick = {},
                onPlayerClick = {},
                onProgressChange = {},
                coverArtUrl = null
            )
        }
    }
}