package com.example.p_music.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.p_music.domain.model.Playlist
import com.example.p_music.presentation.ui.theme.SpotifyColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistDialog(
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onPlaylistSelected: (Playlist) -> Unit,
    onCreateNewPlaylist: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Rounded.PlaylistAdd,
                contentDescription = null,
                tint = SpotifyColors.Green
            )
        },
        title = {
            Text(
                text = "Ajouter à une playlist",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Bouton créer nouvelle playlist
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCreateNewPlaylist() },
                    colors = CardDefaults.cardColors(
                        containerColor = SpotifyColors.Green.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            tint = SpotifyColors.Green
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Créer une nouvelle playlist",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = SpotifyColors.Green
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Liste des playlists
                if (playlists.isEmpty()) {
                    Text(
                        text = "Aucune playlist disponible",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(playlists) { playlist ->
                            PlaylistItem(
                                playlist = playlist,
                                onClick = { onPlaylistSelected(playlist) }
                            )
                        }
                    }
                }
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
private fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = "${playlist.audioCount} chanson${if (playlist.audioCount > 1) "s" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
