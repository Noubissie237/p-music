package com.example.p_music.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.p_music.presentation.viewmodel.VideoPlayerViewModel
import androidx.media3.common.Player

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoSettingsDialog(
    viewModel: VideoPlayerViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val player = viewModel.getExoPlayer()

    var speed by remember { mutableStateOf(player?.playbackParameters?.speed ?: 1f) }
    var isLooping by remember { mutableStateOf(player?.repeatMode == Player.REPEAT_MODE_ONE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                player?.setPlaybackSpeed(speed)
                player?.repeatMode = if (isLooping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                onDismiss()
            }) {
                Text("Valider")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
        title = {
            Text("Paramètres de lecture")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text("Vitesse de lecture : ${String.format("%.1fx", speed)}")
                    Slider(
                        value = speed,
                        onValueChange = { speed = it },
                        valueRange = 0.5f..2.0f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            thumbColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Lecture en boucle")
                    Switch(
                        checked = isLooping,
                        onCheckedChange = { isLooping = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        },
        containerColor = Color(0xFF1A1A1A),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}
