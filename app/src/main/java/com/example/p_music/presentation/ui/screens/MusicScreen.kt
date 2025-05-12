package com.example.p_music.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.p_music.R
import com.example.p_music.domain.model.Audio
import com.example.p_music.presentation.ui.components.MiniPlayer
import com.example.p_music.presentation.ui.components.SpotifyCard
import com.example.p_music.presentation.ui.components.SpotifySearchBar
import com.example.p_music.presentation.ui.theme.SpotifyColors
import com.example.p_music.presentation.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicScreen(
    onAudioClick: (Audio) -> Unit,
    viewModel: MusicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Définir des couleurs Spotify
    val backgroundColor = Color(0xFF121212)
    val surfaceColor = Color(0xFF1A1A1A)
    val primaryColor = SpotifyColors.Green
    val textColor = Color.White
    val secondaryTextColor = Color.LightGray

    // Catégories de bibliothèque simulées pour une interface plus riche
    val categories = listOf("Playlists", "Favoris", "Albums")
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Contenu principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (uiState.currentAudio != null) 72.dp else 0.dp)
        ) {
            // Barre supérieure
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Votre Bibliothèque",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )

                Row {
                    IconButton(onClick = { /* TODO: Open settings */ }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Paramètres",
                            tint = textColor
                        )
                    }
                }
            }

            // Barre de recherche améliorée
            SpotifySearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                onSearch = viewModel::search,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Catégories avec sélection
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.DarkGray.copy(alpha = 0.5f),
                            labelColor = textColor,
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.Black
                        ),
                        border = null
                    )
                }
            }

            Divider(
                color = Color.DarkGray.copy(alpha = 0.3f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Contenu principal - Liste de musique
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(color = primaryColor)
                    }
                    uiState.error != null -> {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = uiState.error!!,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                    uiState.audioList.isEmpty() -> {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MusicOff,
                                contentDescription = null,
                                tint = secondaryTextColor,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.no_music_found),
                                style = MaterialTheme.typography.bodyLarge,
                                color = secondaryTextColor,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            if (uiState.searchQuery.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Essayez avec des termes différents",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = secondaryTextColor.copy(alpha = 0.7f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp, end = 16.dp, top = 8.dp,
                                bottom = if (uiState.currentAudio != null) 80.dp else 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(4.dp) // Espacement réduit pour un look plus épuré
                        ) {
                            itemsIndexed(uiState.audioList) { index, audio ->
                                SpotifyCardEnhanced(
                                    title = audio.title,
                                    subtitle = audio.artist,
                                    imageUrl = audio.coverUri?.toString(),
                                    index = index,
                                    isPlaying = uiState.isPlaying && uiState.currentAudio?.id == audio.id,
                                    onClick = {
                                        viewModel.playAudio(audio)
                                        if (uiState.currentAudio?.id != audio.id) {
                                            onAudioClick(audio)
                                        }
                                    },
                                    audio = audio
                                )
                            }
                        }
                    }
                }
            }
        }

        // MiniPlayer amélioré avec animation
        AnimatedVisibility(
            visible = uiState.currentAudio != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            uiState.currentAudio?.let { audio ->
                MiniPlayer(
                    title = audio.title,
                    artist = audio.artist,
                    isPlaying = uiState.isPlaying,
                    progress = uiState.progress,
                    elapsedTime = uiState.elapsedTime,
                    remainingTime = uiState.remainingTime,
                    onPlayPauseClick = { viewModel.togglePlayPause() },
                    onNextClick = { viewModel.playNext() },
                    onPreviousClick = { viewModel.playPrevious() },
                    onPlayerClick = { onAudioClick(audio) },
                    onProgressChange = { viewModel.seekTo(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SpotifyCardEnhanced(
    title: String,
    subtitle: String,
    imageUrl: String?,
    index: Int,
    isPlaying: Boolean = false,
    onClick: () -> Unit,
    audio: Audio
) {
    val primaryColor = SpotifyColors.Green
    val textColor = Color.White
    val secondaryTextColor = Color.LightGray

    var showBottomSheet by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // Fond transparent pour un look plus épuré
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Numéro de la piste
            Text(
                text = String.format("%02d", index + 1),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = if (isPlaying) primaryColor else secondaryTextColor,
                modifier = Modifier.width(36.dp)
            )

            // Texte
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 16.sp,
                        letterSpacing = 0.sp
                    ),
                    color = if (isPlaying) primaryColor else textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        letterSpacing = 0.sp
                    ),
                    color = secondaryTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Icônes à droite - plus subtiles
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isPlaying) {
                    Icon(
                        imageVector = Icons.Filled.Equalizer,
                        contentDescription = "En lecture",
                        tint = primaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                IconButton(
                    onClick = { showBottomSheet = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "Plus d'options",
                        tint = secondaryTextColor,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }

        // Ligne de séparation subtile
        if (!isPlaying) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 56.dp, end = 8.dp),
                color = Color.DarkGray.copy(alpha = 0.3f),
                thickness = 0.5.dp
            )
        }
    }
    if (showBottomSheet) {
        MusicOptionsBottomSheet(
            audio = audio,
            onDismiss = { showBottomSheet = false },
            // Vous pourrez implémenter ces fonctions plus tard
            onPlayNow = { /* votre logique ici */ },
            onAddToQueue = { /* votre logique ici */ },
            onSetAsRingtone = { /* votre logique ici */ },
            onAddToPlaylist = { /* votre logique ici */ },
            onRename = { /* votre logique ici */ },
            onToggleFavorite = { /* votre logique ici */ },
            onDelete = { /* votre logique ici */ },
            onShare = { /* votre logique ici */ },
            onShowInfo = { /* votre logique ici */ }
        )
    }
}