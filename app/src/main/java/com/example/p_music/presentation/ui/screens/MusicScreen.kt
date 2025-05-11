package com.example.p_music.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.p_music.R
import com.example.p_music.domain.model.Audio
import com.example.p_music.presentation.ui.components.MiniPlayer
import com.example.p_music.presentation.ui.components.SpotifyCard
import com.example.p_music.presentation.ui.components.SpotifySearchBar
import com.example.p_music.presentation.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicScreen(
    onAudioClick: (Audio) -> Unit,
    viewModel: MusicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (uiState.currentAudio != null) 72.dp else 0.dp)
        ) {
            SpotifySearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                onSearch = viewModel::search,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
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
                    uiState.audioList.isEmpty() -> {
                        Text(
                            text = stringResource(R.string.no_music_found),
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
                                SpotifyCard(
                                    title = audio.title,
                                    subtitle = audio.artist,
                                    imageUrl = audio.coverUri?.toString(),
                                    onClick = {
                                        viewModel.playAudio(audio)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // MiniPlayer
        uiState.currentAudio?.let { audio ->
            MiniPlayer(
                title = audio.title,
                artist = audio.artist,
                isPlaying = uiState.isPlaying,
                progress = uiState.progress,
                onPlayPauseClick = { viewModel.togglePlayPause() },
                onNextClick = { /* TODO: Implémenter la logique */ },
                onPreviousClick = { /* TODO: Implémenter la logique */ },
                onPlayerClick = { onAudioClick(audio) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )
        }
    }
} 