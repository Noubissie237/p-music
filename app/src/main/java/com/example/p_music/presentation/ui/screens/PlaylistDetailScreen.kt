package com.example.p_music.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.model.Playlist
import com.example.p_music.presentation.ui.theme.SpotifyColors
import com.example.p_music.presentation.viewmodel.MusicViewModel
import com.example.p_music.presentation.viewmodel.PlaylistViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    onBack: () -> Unit,
    onPlayAudio: (Audio) -> Unit,
    playlistViewModel: PlaylistViewModel = hiltViewModel(),
    musicViewModel: MusicViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var playlist by remember { mutableStateOf<Playlist?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val backgroundColor = Color(0xFF121212)
    val textColor = Color.White

    // Charger la playlist
    LaunchedEffect(playlistId) {
        scope.launch {
            playlistViewModel.playlistRepository.getPlaylistById(playlistId).collect {
                playlist = it
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // En-tête
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Retour",
                        tint = textColor
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = playlist?.name ?: "Chargement...",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Menu
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "Options",
                            tint = textColor
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Modifier") },
                            onClick = {
                                showEditDialog = true
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Rounded.Edit, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Supprimer la playlist") },
                            onClick = {
                                playlist?.let { playlistViewModel.deletePlaylist(it) }
                                showMenu = false
                                onBack()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Rounded.Delete,
                                    contentDescription = null,
                                    tint = Color(0xFFFF5252)
                                )
                            }
                        )
                    }
                }
            }

            // Informations de la playlist
            playlist?.let { pl ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF282828)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.QueueMusic,
                                contentDescription = null,
                                tint = SpotifyColors.Green,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "${pl.audioCount} chanson${if (pl.audioCount > 1) "s" else ""}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = textColor
                                )
                                if (pl.description.isNotEmpty()) {
                                    Text(
                                        text = pl.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Liste des chansons
                if (pl.audios.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MusicNote,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = "Aucune chanson dans cette playlist",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                            Text(
                                text = "Ajoutez des chansons depuis le menu ⋮",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(pl.audios) { audio ->
                            AudioItemInPlaylist(
                                audio = audio,
                                onClick = {
                                    musicViewModel.playAudio(audio)
                                    onPlayAudio(audio)
                                },
                                onRemove = {
                                    playlistViewModel.removeAudioFromPlaylist(playlistId, audio.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialogue de modification
    if (showEditDialog && playlist != null) {
        EditPlaylistDialog(
            playlist = playlist!!,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, description ->
                playlistViewModel.updatePlaylist(playlist!!.copy(name = name, description = description))
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun AudioItemInPlaylist(
    audio: Audio,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF282828)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = null,
                tint = SpotifyColors.Green,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = audio.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = audio.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "Options",
                        tint = Color.LightGray
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Retirer de la playlist") },
                        onClick = {
                            onRemove()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.Remove,
                                contentDescription = null,
                                tint = Color(0xFFFF5252)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EditPlaylistDialog(
    playlist: Playlist,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String) -> Unit
) {
    var playlistName by remember { mutableStateOf(playlist.name) }
    var playlistDescription by remember { mutableStateOf(playlist.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = null,
                tint = SpotifyColors.Green
            )
        },
        title = {
            Text(
                text = "Modifier la playlist",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    label = { Text("Nom de la playlist") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SpotifyColors.Green,
                        focusedLabelColor = SpotifyColors.Green,
                        cursorColor = SpotifyColors.Green
                    )
                )

                OutlinedTextField(
                    value = playlistDescription,
                    onValueChange = { playlistDescription = it },
                    label = { Text("Description (optionnel)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SpotifyColors.Green,
                        focusedLabelColor = SpotifyColors.Green,
                        cursorColor = SpotifyColors.Green
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (playlistName.isNotBlank()) {
                        onConfirm(playlistName.trim(), playlistDescription.trim())
                    }
                },
                enabled = playlistName.isNotBlank()
            ) {
                Text("Enregistrer", color = if (playlistName.isNotBlank()) SpotifyColors.Green else Color.Gray)
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
