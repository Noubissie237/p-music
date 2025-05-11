package com.example.p_music.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.p_music.R
import com.example.p_music.domain.model.Audio
import com.example.p_music.presentation.ui.theme.SpotifyDarkGray
import com.example.p_music.presentation.ui.theme.SpotifyGreen
import com.example.p_music.presentation.ui.theme.SpotifyLightGray
import com.example.p_music.presentation.viewmodel.MusicViewModel
import com.example.p_music.presentation.viewmodel.MusicUiState

@Composable
fun MusicScreen(
    onAudioClick: (Audio) -> Unit,
    viewModel: MusicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage = uiState.error

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
            uiState.audioList.isEmpty() -> {
                Text(
                    text = "Aucun audio trouvé",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.audioList) { audio ->
                        AudioItem(
                            audio = audio,
                            onClick = { onAudioClick(audio) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioItem(
    audio: Audio,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = audio.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = audio.artist,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
} 