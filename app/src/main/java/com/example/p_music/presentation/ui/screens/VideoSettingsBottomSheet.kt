package com.example.p_music.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import com.example.p_music.presentation.viewmodel.VideoPlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoSettingsBottomSheet(
    onDismiss: () -> Unit,
    viewModel: VideoPlayerViewModel = hiltViewModel()
) {
    val player = viewModel.getExoPlayer()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // États pour les différentes options
    var speed by remember { mutableStateOf(player?.playbackParameters?.speed ?: 1f) }
    var isLooping by remember { mutableStateOf(player?.repeatMode == Player.REPEAT_MODE_ONE) }
    var selectedQuality by remember { mutableStateOf("1080p") }

    // Liste des vitesses de lecture (style YouTube)
    val speedOptions = listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)

    // Liste des qualités disponibles
    val qualityOptions = listOf("Auto", "1080p", "720p", "480p", "360p", "240p", "144p")

    // État pour suivre quelle section est ouverte
    var currentSection by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF212121), // Couleur de fond plus sombre comme YouTube
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Box(modifier = Modifier.padding(bottom = 32.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // En-tête avec bouton de fermeture
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Paramètres",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Fermer",
                            tint = Color.White
                        )
                    }
                }

                Divider(color = Color(0xFF303030))

                // Options principales comme sur YouTube
                when (currentSection) {
                    "speed" -> {
                        // Retour à la section principale
                        SettingHeader(
                            icon = Icons.Filled.Speed,
                            title = "Vitesse de lecture",
                            onClick = { currentSection = null }
                        )

                        Divider(color = Color(0xFF303030))

                        // Liste des options de vitesse
                        Column(modifier = Modifier.fillMaxWidth()) {
                            speedOptions.forEach { option ->
                                SpeedOptionItem(
                                    speed = option,
                                    isSelected = speed == option,
                                    onClick = { speed = option }
                                )
                            }
                        }
                    }
                    "quality" -> {
                        // Retour à la section principale
                        SettingHeader(
                            icon = Icons.Filled.Settings,
                            title = "Qualité",
                            onClick = { currentSection = null }
                        )

                        Divider(color = Color(0xFF303030))

                        // Liste des options de qualité
                        Column(modifier = Modifier.fillMaxWidth()) {
                            qualityOptions.forEach { option ->
                                QualityOptionItem(
                                    quality = option,
                                    isSelected = selectedQuality == option,
                                    onClick = { selectedQuality = option }
                                )
                            }
                        }
                    }
                    else -> {
                        // Section principale avec les différentes options
                        SettingItem(
                            icon = Icons.Filled.Speed,
                            title = "Vitesse de lecture",
                            subtitle = "${String.format("%.2fx", speed)}",
                            onClick = { currentSection = "speed" }
                        )

                        SettingItem(
                            icon = Icons.Filled.Settings,
                            title = "Qualité",
                            subtitle = selectedQuality,
                            onClick = { currentSection = "quality" }
                        )

                        RepeatModeSettingItem(
                            isLooping = isLooping,
                            onToggle = { isLooping = !isLooping }
                        )
                    }
                }
            }

            // Bouton d'action flottant seulement quand une section est ouverte
            if (currentSection != null) {
                FloatingActionButton(
                    onClick = {
                        player?.setPlaybackSpeed(speed)
                        player?.repeatMode = if (isLooping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                        // Simuler un changement de qualité (dans un cas réel, cela appellerait une fonction du viewModel)
                        currentSection = null
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Appliquer",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Retour",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun RepeatModeSettingItem(
    isLooping: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isLooping) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                contentDescription = "Mode répétition",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Lecture en boucle",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Switch(
            checked = isLooping,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.DarkGray
            )
        )
    }
}

@Composable
private fun SpeedOptionItem(
    speed: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (speed == 1.0f) "Normale" else String.format("%.2fx", speed),
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
            style = MaterialTheme.typography.bodyLarge
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Sélectionné",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun QualityOptionItem(
    quality: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (quality == "Auto") "Automatique" else quality,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
            style = MaterialTheme.typography.bodyLarge
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Sélectionné",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}