package com.example.p_music.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
    val categories = listOf("Récemment joué", "Favoris", "Albums", "Artistes", "Playlists")
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
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.audioList) { audio ->
                                SpotifyCardEnhanced(
                                    title = audio.title,
                                    subtitle = audio.artist,
                                    imageUrl = audio.coverUri?.toString(),
                                    isPlaying = uiState.isPlaying && uiState.currentAudio?.id == audio.id,
                                    onClick = {
                                        viewModel.playAudio(audio)
                                        if (uiState.currentAudio?.id != audio.id) {
                                            onAudioClick(audio)
                                        }
                                    }
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
                    onProgressChange = { viewModel.seekTo(it) }, // Paramètre manquant ajouté
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
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
    isPlaying: Boolean = false,
    onClick: () -> Unit
) {
    val primaryColor = SpotifyColors.Green
    val textColor = Color.White
    val secondaryTextColor = Color.LightGray
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF282828)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image d'artwork
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl != null) {
                    // AsyncImage nécessaire
                    // AsyncImage(
                    //    model = imageUrl,
                    //    contentDescription = title,
                    //    contentScale = ContentScale.Crop,
                    //    modifier = Modifier.fillMaxSize()
                    // )
                    
                    // Placeholder pour le moment
                    Icon(
                        imageVector = Icons.Rounded.MusicNote,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.MusicNote,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Indicateur de lecture
                if (isPlaying) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color(0x80000000))
                            .padding(4.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayCircleFilled,
                            contentDescription = "En lecture",
                            tint = primaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Texte
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isPlaying) primaryColor else textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = secondaryTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Bouton de menu
            IconButton(
                onClick = { /* Menu options */ },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "Plus d'options",
                    tint = secondaryTextColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}