package com.example.p_music.presentation.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    // Obtenir la hauteur de l'écran pour définir la hauteur max du BottomSheet
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val maxSheetHeight = screenHeight * 0.8f // 80% de la hauteur de l'écran

    // État de défilement pour le contenu
    val scrollState = rememberScrollState()

    // Configuration personnalisée du BottomSheet
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Couleurs Spotify avec des verts subtils
    val spotifyDarkColor = Color(0xFF121212)
    val spotifyDarkGreen = Color(0xFF1E3B2E)
    val spotifyLightGreen = Color(0xFF1ED760) // Vert Spotify clair

    val primaryColor = spotifyLightGreen // Utiliser le vert Spotify
    val textColor = Color.White
    val cardColor = Color(0xFF282828)

    // État pour suivre l'option active/pressée
    var activeOption by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent, // Transparent pour pouvoir utiliser notre propre gradient
        dragHandle = {
            // Poignée de glissement personnalisée
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.Gray.copy(alpha = 0.6f))
                )
            }
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        windowInsets = WindowInsets(0, 0, 0, 0), // Supprime les insets par défaut
        modifier = Modifier.heightIn(max = maxSheetHeight) // Limite la hauteur maximale
    ) {
        // Fond avec gradient Spotify
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            spotifyDarkGreen, // Vert foncé en haut
                            spotifyDarkColor  // Noir en bas
                        ),
                        startY = 0f,
                        endY = 1200f // Valeur élevée pour un dégradé très progressif
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 32.dp) // Ajout d'un padding supérieur supplémentaire
            ) {
                // En-tête avec informations sur le morceau - RESTE FIXE
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Pochette de l'album avec ombre et style amélioré
                            Card(
                                modifier = Modifier.size(56.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = cardColor),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.MusicNote,
                                        contentDescription = null,
                                        tint = spotifyLightGreen, // Icône en vert Spotify
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Informations sur le morceau
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = audio.title,
                                    color = textColor,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.25).sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = audio.artist,
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Bouton de fermeture élégant
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.DarkGray.copy(alpha = 0.3f))
                                    .clickable { onDismiss() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Fermer",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Divider(
                            color = spotifyLightGreen.copy(alpha = 0.2f), // Séparateur vert subtil
                            thickness = 1.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Espace supplémentaire avant le contenu scrollable

                // PARTIE SCROLLABLE - Contient toutes les options
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState) // Ajout du défilement vertical ici
                ) {
                    // Section 1: Options principales
                    OptionGroup(
                        title = "Lecture",
                        options = listOf(
                            OptionItem(
                                id = "play",
                                icon = Icons.Rounded.PlayArrow,
                                title = "Écouter maintenant",
                                onClick = onPlayNow
                            ),
                            OptionItem(
                                id = "queue",
                                icon = Icons.Rounded.QueueMusic,
                                title = "Écouter à la suite",
                                onClick = onAddToQueue
                            ),
                            OptionItem(
                                id = "ringtone",
                                icon = Icons.Rounded.Notifications,
                                title = "Définir comme sonnerie",
                                onClick = onSetAsRingtone
                            ),
                            OptionItem(
                                id = "playlist",
                                icon = Icons.Rounded.PlaylistAdd,
                                title = "Ajouter à une playlist",
                                onClick = onAddToPlaylist
                            )
                        ),
                        activeOption = activeOption,
                        onActiveChange = { activeOption = it },
                        primaryColor = primaryColor,
                        onDismiss = onDismiss
                    )

                    // Section 2: Gestion du morceau
                    OptionGroup(
                        title = "Gestion",
                        options = listOf(
                            OptionItem(
                                id = "rename",
                                icon = Icons.Rounded.Edit,
                                title = "Renommer",
                                onClick = onRename
                            ),
                            OptionItem(
                                id = "favorite",
                                icon = if (audio.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                title = if (audio.isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                                onClick = onToggleFavorite
                            ),
                            OptionItem(
                                id = "delete",
                                icon = Icons.Rounded.Delete,
                                title = "Supprimer",
                                onClick = onDelete,
                                isDestructive = true
                            )
                        ),
                        activeOption = activeOption,
                        onActiveChange = { activeOption = it },
                        primaryColor = primaryColor,
                        onDismiss = onDismiss
                    )

                    // Section 3: Divers
                    OptionGroup(
                        title = "Plus",
                        options = listOf(
                            OptionItem(
                                id = "share",
                                icon = Icons.Rounded.Share,
                                title = "Partager",
                                onClick = onShare
                            ),
                            OptionItem(
                                id = "info",
                                icon = Icons.Rounded.Info,
                                title = "Informations",
                                onClick = onShowInfo
                            )
                        ),
                        activeOption = activeOption,
                        onActiveChange = { activeOption = it },
                        primaryColor = primaryColor,
                        onDismiss = onDismiss,
                        showDivider = false // Pas de diviseur après le dernier groupe
                    )

                    // Espace supplémentaire en bas pour améliorer l'expérience de défilement
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// Le reste du code reste inchangé
data class OptionItem(
    val id: String,
    val icon: ImageVector,
    val title: String,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit
)

@Composable
private fun OptionGroup(
    title: String,
    options: List<OptionItem>,
    activeOption: String?,
    onActiveChange: (String?) -> Unit,
    primaryColor: Color,
    onDismiss: () -> Unit,
    showDivider: Boolean = true
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        // Titre du groupe d'options - en vert Spotify
        Text(
            text = title,
            color = primaryColor.copy(alpha = 0.7f), // Titre en vert avec transparence
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        // Options du groupe
        options.forEach { option ->
            EnhancedMusicOptionItem(
                id = option.id,
                icon = option.icon,
                title = option.title,
                isActive = activeOption == option.id,
                isDestructive = option.isDestructive,
                onActiveChange = onActiveChange,
                primaryColor = primaryColor,
                onClick = {
                    option.onClick()
                    onDismiss()
                }
            )
        }

        if (showDivider) {
            Divider(
                color = primaryColor.copy(alpha = 0.15f), // Séparateur vert subtil
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun EnhancedMusicOptionItem(
    id: String,
    icon: ImageVector,
    title: String,
    isActive: Boolean,
    isDestructive: Boolean,
    onActiveChange: (String?) -> Unit,
    primaryColor: Color,
    onClick: () -> Unit
) {
    // Couleurs animées pour une transition fluide - remplacer par des tons de vert
    val backgroundColor = animateColorAsState(
        targetValue = if (isActive) Color(0xFF1E3B2E) else Color.Transparent, // Fond vert foncé pour l'item actif
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
        label = "bgColor"
    )

    val contentColor = if (isDestructive) {
        Color(0xFFFF5252) // Rouge pour l'option de suppression
    } else if (isActive) {
        primaryColor // Vert Spotify pour l'élément actif
    } else {
        Color.White // Couleur par défaut
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(backgroundColor.value)
            .clickable {
                onActiveChange(id)
                onClick()
            }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cercle d'arrière-plan pour l'icône - avec couleur Spotify
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isDestructive) contentColor.copy(alpha = 0.1f) else primaryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = contentColor,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
                letterSpacing = 0.sp
            )
        )
    }
}