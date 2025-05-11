package com.example.p_music.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.p_music.domain.model.Audio
import com.example.p_music.presentation.viewmodel.MusicViewModel

@Composable
fun MusicScreen(
    onAudioClick: (Audio) -> Unit,
    viewModel: MusicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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