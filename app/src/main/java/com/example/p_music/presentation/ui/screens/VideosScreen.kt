package com.example.p_music.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.p_music.domain.model.Video
import com.example.p_music.presentation.viewmodel.VideosViewModel
import java.io.File
import java.util.concurrent.TimeUnit

// Couleurs inspirées de Spotify
private val SpotifyBlack = Color(0xFF121212)
private val SpotifyDarkGray = Color(0xFF212121)
private val SpotifyGreen = Color(0xFF1DB954)
private val SpotifyLightGray = Color(0xFFB3B3B3)
private val SpotifyWhite = Color(0xFFFFFFFF)

@Composable
fun VideosScreen(
    viewModel: VideosViewModel = hiltViewModel(),
    onVideoClick: (Video) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SpotifyBlack)
            .padding(16.dp)
    ) {
        Text(
            text = "Vidéos",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = SpotifyWhite
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = SpotifyGreen
                    )
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Erreur de chargement",
                            color = SpotifyWhite,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${uiState.error}",
                            color = SpotifyLightGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { /* Rafraîchir */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SpotifyGreen
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("Réessayer")
                        }
                    }
                }
            }
            uiState.videos.isEmpty() -> {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
                    Text(
                        text = "Aucune vidéo disponible",
                        color = SpotifyLightGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.videos) { video ->
                        VideoRowItem(
                            video = video,
                            onVideoClick = { onVideoClick(video) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoRowItem(
    video: Video,
    onVideoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    // Pour simuler les données supplémentaires
    val videoSize = "12.7 MB"
    val videoPath = "/storage/emulated/0/DCIM/Snapchat"
    val folderName = videoPath.split("/").last()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SpotifyDarkGray)
            .clickable(onClick = onVideoClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Première colonne - Miniature vidéo
        Box(
            modifier = Modifier
                .width(180.dp)
                .fillMaxHeight()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(video.thumbnailUri)
                    .crossfade(true)
                    .build(),
                contentDescription = video.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Durée de la vidéo
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = formatDuration(video.duration.toMillis()),
                    style = MaterialTheme.typography.bodySmall,
                    color = SpotifyWhite
                )
            }
        }

        // Deuxième colonne - Informations
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Informations de la vidéo
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = SpotifyWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = videoSize,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SpotifyLightGray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = folderName,
                    style = MaterialTheme.typography.bodySmall,
                    color = SpotifyLightGray.copy(alpha = 0.7f)
                )
            }

            // Icône pour le menu
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = SpotifyLightGray
                    )
                }

                // Menu déroulant
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(SpotifyDarkGray)
                ) {
                    DropdownMenuItem(
                        text = { Text("Renommer", color = SpotifyWhite) },
                        onClick = {
                            showMenu = false
                            // Action renommer
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("File info", color = SpotifyWhite) },
                        onClick = {
                            showMenu = false
                            // Action info fichier
                        }
                    )
                }
            }
        }
    }
}

// Scaffold qui serait affiché au clic sur le menu
@Composable
private fun VideoOptionsScaffold(
    video: Video,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onFileInfo: () -> Unit
) {
    // Cette fonction reste vide car vous ne souhaitez pas l'implémentation
    // mais juste montrer la structure
}

private fun formatDuration(durationMs: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
    return String.format("%02d:%02d", minutes, seconds)
}