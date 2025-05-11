package com.example.p_music.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import kotlin.math.sin

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
    
    // Animation pour l'égaliseur
    val infiniteTransition = rememberInfiniteTransition(label = "Equalizer")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Phase"
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
            
            // Image principale
            Image(
                painter = painterResource(id = R.drawable.me),
                contentDescription = "Pochette",
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Égaliseur animé
            if (uiState.isPlaying) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(vertical = 16.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    val barCount = 20
                    val barWidth = width / barCount
                    
                    for (i in 0 until barCount) {
                        val x = i * barWidth + barWidth / 2
                        val amplitude = height * 0.4f
                        val frequency = 2f
                        val offset = i * 0.2f
                        
                        val y1 = height / 2 + amplitude * sin(phase + offset) * sin(phase * frequency)
                        val y2 = height / 2 - amplitude * sin(phase + offset) * sin(phase * frequency)
                        
                        drawLine(
                            color = SpotifyColors.Green,
                            start = Offset(x, y1),
                            end = Offset(x, y2),
                            strokeWidth = barWidth * 0.8f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Informations de la piste
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
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Barre de progression
            Slider(
                value = uiState.progress,
                onValueChange = { viewModel.seekTo(it) },
                colors = SliderDefaults.colors(
                    thumbColor = primaryColor,
                    activeTrackColor = primaryColor,
                    inactiveTrackColor = Color.DarkGray
                )
            )
            
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
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Contrôles de lecture
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.toggleShuffleMode() }) {
                    Icon(
                        imageVector = Icons.Filled.Shuffle,
                        contentDescription = "Lecture aléatoire",
                        tint = if (uiState.isShuffleMode) primaryColor else textColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                IconButton(onClick = { viewModel.playPrevious() }) {
                    Icon(
                        imageVector = Icons.Rounded.SkipPrevious,
                        contentDescription = "Précédent",
                        tint = textColor,
                        modifier = Modifier.size(56.dp)
                    )
                }
                
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
                
                IconButton(onClick = { viewModel.playNext() }) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = "Suivant",
                        tint = textColor,
                        modifier = Modifier.size(56.dp)
                    )
                }
                
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