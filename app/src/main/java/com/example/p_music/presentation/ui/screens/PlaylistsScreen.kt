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
import com.example.p_music.domain.model.Playlist
import com.example.p_music.presentation.ui.components.CreatePlaylistDialog
import com.example.p_music.presentation.ui.theme.SpotifyColors
import com.example.p_music.presentation.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    onPlaylistClick: (Long) -> Unit = {},
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    val playlists by viewModel.playlists.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    val backgroundColor = Color(0xFF121212)
    val textColor = Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // En-tête
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mes Playlists",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )

                IconButton(
                    onClick = { showCreateDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Créer une playlist",
                        tint = SpotifyColors.Green,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Liste des playlists
            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlaylistAdd,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Aucune playlist",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SpotifyColors.Green
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Créer une playlist")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(playlists) { playlist ->
                        PlaylistCard(
                            playlist = playlist,
                            onClick = { onPlaylistClick(playlist.id) },
                            onDelete = { viewModel.deletePlaylist(playlist) }
                        )
                    }
                }
            }
        }
    }

    // Dialogue de création
    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, description ->
                viewModel.createPlaylist(name, description)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun PlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit,
    onDelete: () -> Unit
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icône
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        SpotifyColors.Green.copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.QueueMusic,
                    contentDescription = null,
                    tint = SpotifyColors.Green,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Infos
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${playlist.audioCount} chanson${if (playlist.audioCount > 1) "s" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                if (playlist.description.isNotEmpty()) {
                    Text(
                        text = playlist.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        maxLines = 1
                    )
                }
            }

            // Menu
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
                        text = { Text("Supprimer") },
                        onClick = {
                            onDelete()
                            showMenu = false
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
    }
} 