package com.example.p_music.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.p_music.domain.model.Audio
import com.example.p_music.presentation.ui.theme.SpotifyColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicOptionsBottomSheet(
    audio: Audio,
    onDismiss: () -> Unit,
    onPlayNow: () -> Unit = {},
    onAddToQueue: () -> Unit = {},
    onSetAsRingtone: () -> Unit = {},
    onAddToPlaylist: () -> Unit = {},
    onRename: () -> Unit = {},
    onToggleFavorite: () -> Unit = {},
    onDelete: () -> Unit = {},
    onShare: () -> Unit = {},
    onShowInfo: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val primaryColor = SpotifyColors.Green
    val textColor = Color.White
    val backgroundColor = Color(0xFF282828)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = backgroundColor,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            // En-tête avec informations sur le morceau
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Image de la chanson
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF333333)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MusicNote,
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = audio.title,
                            color = textColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = audio.artist,
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Fermer",
                        tint = textColor
                    )
                }
            }

            Divider(color = Color.DarkGray.copy(alpha = 0.5f))

            // Liste des options
            MusicOptionItem(
                icon = Icons.Rounded.PlayArrow,
                title = "Écouter maintenant",
                onClick = {
                    onPlayNow()
                    onDismiss()
                }
            )

            MusicOptionItem(
                icon = Icons.Rounded.QueueMusic,
                title = "Écouter à la suite",
                onClick = {
                    onAddToQueue()
                    onDismiss()
                }
            )

            MusicOptionItem(
                icon = Icons.Rounded.Notifications,
                title = "Définir comme sonnerie",
                onClick = {
                    onSetAsRingtone()
                    onDismiss()
                }
            )

            MusicOptionItem(
                icon = Icons.Rounded.PlaylistAdd,
                title = "Ajouter à une playlist",
                onClick = {
                    onAddToPlaylist()
                    onDismiss()
                }
            )

            MusicOptionItem(
                icon = Icons.Rounded.Edit,
                title = "Renommer",
                onClick = {
                    onRename()
                    onDismiss()
                }
            )

            MusicOptionItem(
                icon = Icons.Rounded.Favorite,
                title = "Favoris",
                onClick = {
                    onToggleFavorite()
                    onDismiss()
                }
            )

            MusicOptionItem(
                icon = Icons.Rounded.Delete,
                title = "Supprimer",
                onClick = {
                    onDelete()
                    onDismiss()
                }
            )

            MusicOptionItem(
                icon = Icons.Rounded.Share,
                title = "Partager",
                onClick = {
                    onShare()
                    onDismiss()
                }
            )

            MusicOptionItem(
                icon = Icons.Rounded.Info,
                title = "Informations",
                onClick = {
                    onShowInfo()
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun MusicOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}