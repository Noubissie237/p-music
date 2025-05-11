package com.example.p_music.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.p_music.presentation.ui.components.SpotifyCard
import com.example.p_music.presentation.utils.formatDuration
import com.example.p_music.presentation.viewmodel.AudioPlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    onNavigateBack: () -> Unit,
    audioId: String,
    viewModel: AudioPlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(audioId) {
        viewModel.loadAudio(audioId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lecture") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image de l'audio
            SpotifyCard(
                title = uiState.currentAudio?.title ?: "",
                subtitle = uiState.currentAudio?.artist ?: "",
                imageUrl = uiState.currentAudio?.coverUri?.toString(),
                onClick = { viewModel.togglePlayPause() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            // Barre de progression
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .padding(horizontal = 16.dp)
            ) {
                // Fond de la barre
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                // Progression
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(uiState.progress)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            // Temps écoulé et restant
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(uiState.elapsedTime),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = formatDuration(uiState.remainingTime),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Contrôles de lecture
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.playPrevious() }) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Précédent",
                        modifier = Modifier.size(48.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.togglePlayPause() },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (uiState.isPlaying) "Pause" else "Lecture",
                        modifier = Modifier.size(48.dp)
                    )
                }

                IconButton(onClick = { viewModel.playNext() }) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Suivant",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
} 