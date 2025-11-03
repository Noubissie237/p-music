package com.example.p_music.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.p_music.presentation.ui.theme.SpotifyColors

@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String) -> Unit
) {
    var playlistName by remember { mutableStateOf("") }
    var playlistDescription by remember { mutableStateOf("") }

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
                text = "Nouvelle playlist",
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
                Text("Créer", color = if (playlistName.isNotBlank()) SpotifyColors.Green else Color.Gray)
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
