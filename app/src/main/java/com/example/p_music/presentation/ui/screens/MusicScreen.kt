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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@Composable
fun MusicScreen(
    viewModel: MusicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpotifyDarkGray)
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = SpotifyGreen
                )
            }
            uiState.error != null -> {
                Text(
                    text = "Erreur : ${uiState.error}",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            uiState.audioList.isEmpty() -> {
                Text(
                    text = "Aucune musique trouvée",
                    color = SpotifyLightGray,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.audioList) { audio ->
                        AudioItem(audio = audio)
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioItem(audio: Audio) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        colors = CardDefaults.cardColors(
            containerColor = SpotifyDarkGray.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Miniature par défaut
            Image(
                painter = painterResource(id = R.drawable.ic_music_note),
                contentDescription = "Miniature",
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            // Informations
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = audio.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = audio.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SpotifyLightGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Bouton Lecture
            IconButton(
                onClick = { /* TODO: Implémenter la lecture */ },
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SpotifyGreen)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Lire",
                    tint = Color.White
                )
            }
        }
    }
} 