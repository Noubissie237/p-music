package com.example.p_music.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.p_music.R
import com.example.p_music.presentation.ui.components.*
import com.example.p_music.presentation.ui.theme.SpotifyColors
import com.example.p_music.presentation.utils.formatDuration
import com.example.p_music.presentation.viewmodel.AudioPlayerViewModel

@Composable
fun AudioPlayerScreen(
    onNavigateBack: () -> Unit,
    audioId: String,
    viewModel: AudioPlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Animation pour les effets visuels
    val albumScale by animateFloatAsState(
        targetValue = if (uiState.isPlaying) 1f else 0.96f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "AlbumScale"
    )
    
    // Couleurs dérivées de l'artwork (dans un cas réel, vous utiliseriez Palette API)
    val primaryColor = SpotifyColors.Green
    val backgroundColor = Color(0xFF121212)
    val textColor = Color.White
    val secondaryTextColor = Color.LightGray
    
    // État pour simuler un favori
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(audioId) {
        viewModel.loadAudio(audioId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Fond d'écran avec artwork flou
        uiState.currentAudio?.coverUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(20.dp)
                    .alpha(0.3f),
                contentScale = ContentScale.FillHeight
            )
        }
        
        // Overlay gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            backgroundColor.copy(alpha = 0.1f),
                            backgroundColor.copy(alpha = 0.8f),
                            backgroundColor.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        // Contenu principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Barre supérieure
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Retour",
                        tint = textColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LECTURE EN COURS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = secondaryTextColor
                    )
                }
                
                // Bouton d'options
                IconButton(onClick = { /* TODO: Show options */ }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "Plus d'options",
                        tint = textColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Artwork principal
            Box(
                modifier = Modifier
                    .size(with(LocalDensity.current) {
                        LocalConfiguration.current.screenWidthDp.dp - 80.dp
                    })
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .scale(albumScale)
                    .graphicsLayer {
                        shadowElevation = 16f
                        shape = RoundedCornerShape(8.dp)
                    }
            ) {
                uiState.currentAudio?.coverUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Pochette de ${uiState.currentAudio?.title}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } ?: run {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_music_note),
                            contentDescription = "Pochette par défaut",
                            modifier = Modifier.size(80.dp),
                            tint = Color.LightGray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Informations de la piste avec bouton like
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Titre et artiste
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = uiState.currentAudio?.title ?: "",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = uiState.currentAudio?.artist ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = secondaryTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Bouton like
                IconButton(onClick = { isFavorite = !isFavorite }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Ajouter aux favoris",
                        tint = if (isFavorite) SpotifyColors.Green else textColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Barre de progression personnalisée
            Column(modifier = Modifier.fillMaxWidth()) {
                // Slider personnalisé
                Slider(
                    value = uiState.progress,
                    onValueChange = { viewModel.seekTo(it) },
                    colors = SliderDefaults.colors(
                        thumbColor = primaryColor,
                        activeTrackColor = primaryColor,
                        inactiveTrackColor = Color.DarkGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Temps écoulé/restant
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDuration(uiState.elapsedTime),
                        style = MaterialTheme.typography.bodySmall,
                        color = secondaryTextColor
                    )
                    
                    Text(
                        text = "-" + formatDuration(uiState.remainingTime),
                        style = MaterialTheme.typography.bodySmall,
                        color = secondaryTextColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Contrôles de lecture principaux
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bouton shuffle
                IconButton(onClick = { viewModel.toggleShuffleMode() }) {
                    Icon(
                        imageVector = Icons.Filled.Shuffle,
                        contentDescription = "Lecture aléatoire",
                        tint = if (uiState.isShuffleMode) primaryColor else textColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Bouton précédent
                IconButton(
                    onClick = { viewModel.playPrevious() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipPrevious,
                        contentDescription = "Précédent",
                        tint = textColor,
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                // Bouton play/pause
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(textColor)
                        .clickable { viewModel.togglePlayPause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (uiState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (uiState.isPlaying) "Pause" else "Lecture",
                        tint = backgroundColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Bouton suivant
                IconButton(
                    onClick = { viewModel.playNext() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = "Suivant",
                        tint = textColor,
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                // Bouton repeat
                IconButton(onClick = { viewModel.toggleRepeatMode() }) {
                    Icon(
                        imageVector = if (uiState.isRepeatMode) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                        contentDescription = "Lecture en boucle",
                        tint = if (uiState.isRepeatMode) primaryColor else textColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Rangée inférieure optionnelle avec appareil connecté et file d'attente
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Indication de l'appareil de lecture
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.DarkGray.copy(alpha = 0.3f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.DevicesOther,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cet appareil",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor
                    )
                }
                
                // Bouton file d'attente
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.DarkGray.copy(alpha = 0.3f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable { /* Show queue */ },
                ) {
                    Icon(
                        imageVector = Icons.Filled.QueueMusic,
                        contentDescription = "Voir la file d'attente",
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "File d'attente",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor
                    )
                }
            }
        }
        
        // Visualiseur d'égaliseur en fond (uniquement visible pendant la lecture)
        AnimatedVisibility(
            visible = uiState.isPlaying,
            enter = fadeIn(animationSpec = tween(1000)),
            exit = fadeOut(animationSpec = tween(1000)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 220.dp)
        ) {
            // Ajoutez ici votre égaliseur SVG animé
            Box(
                modifier = Modifier
                    .size(width = 120.dp, height = 40.dp)
                    .alpha(0.5f)
            ) {
                // Vous pourriez intégrer votre égaliseur SVG ici
            }
        }
    }
}