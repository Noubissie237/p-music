package com.example.p_music.presentation.ui.screens

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.p_music.R
import com.example.p_music.domain.model.Audio
import com.example.p_music.presentation.ui.components.MiniPlayer
import com.example.p_music.presentation.ui.components.SpotifyCard
import com.example.p_music.presentation.ui.components.SpotifySearchBar
import com.example.p_music.presentation.ui.theme.SpotifyColors
import com.example.p_music.presentation.viewmodel.MusicViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicScreen(
    onAudioClick: (Audio) -> Unit,
    viewModel: MusicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // États pour les dialogues
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var audioToDelete by remember { mutableStateOf<Audio?>(null) }
    
    // Launcher pour la permission de suppression (Android 10+)
    val deletePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "Fichier supprimé avec succès", Toast.LENGTH_SHORT).show()
            viewModel.loadAudios() // Recharger la liste
        } else {
            Toast.makeText(context, "Suppression annulée", Toast.LENGTH_SHORT).show()
        }
        audioToDelete = null
    }

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
                                    audio = audio,
                                    onDelete = { audio ->
                                        audioToDelete = audio
                                        showDeleteDialog = true
                                    },
                                    onShare = { audio ->
                                        shareAudio(context, audio)
                                    },
                                    onShowInfo = { audio ->
                                        audioToDelete = audio
                                        showInfoDialog = true
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
                    onProgressChange = { viewModel.seekTo(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
    
    // Dialogue de confirmation de suppression
    if (showDeleteDialog && audioToDelete != null) {
        DeleteConfirmationDialog(
            audio = audioToDelete!!,
            onConfirm = {
                deleteAudio(context, audioToDelete!!, deletePermissionLauncher)
                showDeleteDialog = false
            },
            onDismiss = {
                showDeleteDialog = false
                audioToDelete = null
            }
        )
    }
    
    // Dialogue d'informations
    if (showInfoDialog && audioToDelete != null) {
        AudioInfoDialog(
            audio = audioToDelete!!,
            onDismiss = {
                showInfoDialog = false
                audioToDelete = null
            }
        )
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
    audio: Audio,
    onDelete: (Audio) -> Unit,
    onShare: (Audio) -> Unit,
    onShowInfo: (Audio) -> Unit
) {
    val primaryColor = SpotifyColors.Green
    val textColor = Color.White
    val secondaryTextColor = Color.LightGray
    val context = LocalContext.current

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
            onPlayNow = onClick,
            onAddToQueue = { /* TODO: Implémenter */ },
            onSetAsRingtone = { /* TODO: Implémenter */ },
            onAddToPlaylist = { /* TODO: Implémenter */ },
            onRename = { /* TODO: Implémenter */ },
            onToggleFavorite = { /* TODO: Implémenter */ },
            onDelete = { onDelete(audio) },
            onShare = { onShare(audio) },
            onShowInfo = { onShowInfo(audio) }
        )
    }
}

// Fonction pour partager un fichier audio
private fun shareAudio(context: android.content.Context, audio: Audio) {
    try {
        val file = File(audio.path)
        if (file.exists()) {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, audio.title)
                putExtra(Intent.EXTRA_TEXT, "Écoute ${audio.title} par ${audio.artist}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(shareIntent, "Partager via"))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// Fonction pour supprimer un fichier audio
private fun deleteAudio(
    context: android.content.Context,
    audio: Audio,
    permissionLauncher: androidx.activity.result.ActivityResultLauncher<IntentSenderRequest>
) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ : Utiliser createDeleteRequest
            val uris = listOf(audio.uri)
            val pendingIntent = MediaStore.createDeleteRequest(context.contentResolver, uris)
            
            val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()
            permissionLauncher.launch(intentSenderRequest)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 : Utiliser RecoverableSecurityException
            try {
                context.contentResolver.delete(audio.uri, null, null)
                Toast.makeText(context, "Fichier supprimé avec succès", Toast.LENGTH_SHORT).show()
            } catch (securityException: SecurityException) {
                val recoverableSecurityException = securityException as? 
                    android.app.RecoverableSecurityException
                    ?: throw securityException
                
                val intentSenderRequest = IntentSenderRequest.Builder(
                    recoverableSecurityException.userAction.actionIntent.intentSender
                ).build()
                permissionLauncher.launch(intentSenderRequest)
            }
        } else {
            // Android 9 et moins : Suppression directe
            val deleted = context.contentResolver.delete(audio.uri, null, null)
            if (deleted > 0) {
                Toast.makeText(context, "Fichier supprimé avec succès", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Échec de la suppression", Toast.LENGTH_SHORT).show()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

// Dialogue de confirmation de suppression
@Composable
fun DeleteConfirmationDialog(
    audio: Audio,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isAndroid10Plus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = null,
                tint = Color(0xFFFF5252)
            )
        },
        title = {
            Text(
                text = "Supprimer le fichier ?",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Voulez-vous vraiment supprimer \"${audio.title}\" ?",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (isAndroid10Plus) {
                    Text(
                        text = "ℹ️ Android vous demandera une confirmation système pour des raisons de sécurité.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                } else {
                    Text(
                        text = "⚠️ Cette action est irréversible.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF5252)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFFF5252)
                )
            ) {
                Text("Supprimer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
        containerColor = Color(0xFF1E1E1E),
        textContentColor = Color.White
    )
}

// Dialogue d'informations sur l'audio
@Composable
fun AudioInfoDialog(
    audio: Audio,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
                tint = SpotifyColors.Green
            )
        },
        title = {
            Text(
                text = "Informations",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoRow("Titre", audio.title)
                InfoRow("Artiste", audio.artist)
                InfoRow("Album", audio.album)
                InfoRow("Durée", formatDuration(audio.duration))
                InfoRow("Taille", formatFileSize(audio.size))
                InfoRow("Chemin", audio.path)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        },
        containerColor = Color(0xFF1E1E1E),
        textContentColor = Color.White
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}