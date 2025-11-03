package com.example.p_music.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.p_music.R
import com.example.p_music.presentation.navigation.Screen
import com.example.p_music.presentation.ui.components.MiniPlayer
import com.example.p_music.presentation.viewmodel.AudioPlayerViewModel
import com.example.p_music.presentation.viewmodel.MusicViewModel

// Couleurs style Spotify pour la barre de navigation
private val AppSpotifyGreen = Color(0xFF1DB954)
private val AppSpotifyBlack = Color(0xFF121212)
private val AppSpotifyLightGrey = Color(0xFFB3B3B3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
//    val musicViewModel: MusicViewModel = hiltViewModel()
    val audioPlayerViewModel: AudioPlayerViewModel = hiltViewModel()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination

    // Détermine si nous sommes sur la page de lecture
    val isPlayerScreen = currentDestination?.route?.startsWith("player/") == true ||
            currentDestination?.route?.startsWith("video_player/") == true

    // État pour contrôler la visibilité du MiniPlayer
    var showMiniPlayer by remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            if (!isPlayerScreen) {
                SpotifyStyleBottomNavigation(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Music.route
            ) {
                composable(Screen.Music.route) {
                    MusicScreen(
                        onAudioClick = { audio ->
                            // Réaffiche le mini player si une nouvelle chanson est sélectionnée
                            showMiniPlayer = true
                            navController.navigate("player/${audio.id}")
                        }
                    )
                }
                composable(Screen.Videos.route) {
                    VideosScreen(
                        onVideoClick = { video ->
                            navController.navigate("video_player/${video.id}")
                        }
                    )
                }
                composable(Screen.Favorites.route) {
                    FavoritesScreen()
                }
                composable(Screen.Playlists.route) {
                    PlaylistsScreen(
                        onPlaylistClick = { playlistId ->
                            navController.navigate(Screen.PlaylistDetail.createRoute(playlistId))
                        }
                    )
                }
                composable(
                    route = Screen.PlaylistDetail.route,
                    arguments = Screen.PlaylistDetail.arguments
                ) { backStackEntry ->
                    val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: return@composable
                    PlaylistDetailScreen(
                        playlistId = playlistId,
                        onBack = { navController.popBackStack() },
                        onPlayAudio = { audio ->
                            navController.navigate("player/${audio.id}")
                        }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen()
                }
                composable("player/{audioId}") { backStackEntry ->
                    val audioId = backStackEntry.arguments?.getString("audioId") ?: return@composable
                    AudioPlayerScreen(
                        onNavigateBack = { navController.popBackStack() },
                        audioId = audioId
                    )
                }
                composable("video_player/{videoId}") { backStackEntry ->
                    val videoId = backStackEntry.arguments?.getString("videoId") ?: return@composable
                    VideoPlayerScreen(
                        onNavigateBack = { navController.popBackStack() },
                        videoId = videoId
                    )
                }
            }

            // Mini player original avec bouton de fermeture
            if (!isPlayerScreen && showMiniPlayer) {
                val uiState by audioPlayerViewModel.uiState.collectAsState()
                if (uiState.currentAudio != null) {
                    // On ajoute une Box pour pouvoir positionner le bouton de fermeture
                    Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                        MiniPlayer(
                            modifier = Modifier.fillMaxWidth(),
                            title = uiState.currentAudio?.title ?: "",
                            artist = uiState.currentAudio?.artist ?: "",
                            isPlaying = uiState.isPlaying,
                            progress = uiState.progress,
                            elapsedTime = uiState.elapsedTime,
                            remainingTime = uiState.remainingTime,
                            onPlayPauseClick = { audioPlayerViewModel.togglePlayPause() },
                            onNextClick = { audioPlayerViewModel.playNext() },
                            onPreviousClick = { audioPlayerViewModel.playPrevious() },
                            onPlayerClick = {
                                uiState.currentAudio?.id?.let { audioId ->
                                    navController.navigate("player/$audioId")
                                }
                            },
                            onProgressChange = { audioPlayerViewModel.seekTo(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpotifyStyleBottomNavigation(
    navController: androidx.navigation.NavController,
    currentDestination: androidx.navigation.NavDestination?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AppSpotifyBlack,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Rounded.Home,
                label = stringResource(R.string.music),
                isSelected = currentDestination?.hierarchy?.any { it.route == Screen.Music.route } == true,
                onClick = {
                    navController.navigate(Screen.Music.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            BottomNavItem(
                icon = Icons.Rounded.VideoLibrary,
                label = stringResource(R.string.videos),
                isSelected = currentDestination?.hierarchy?.any { it.route == Screen.Videos.route } == true,
                onClick = {
                    navController.navigate(Screen.Videos.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            BottomNavItem(
                icon = Icons.Rounded.Favorite,
                label = stringResource(R.string.favorites),
                isSelected = currentDestination?.hierarchy?.any { it.route == Screen.Favorites.route } == true,
                onClick = {
                    navController.navigate(Screen.Favorites.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            BottomNavItem(
                icon = Icons.Rounded.PlaylistPlay,
                label = stringResource(R.string.playlists),
                isSelected = currentDestination?.hierarchy?.any { it.route == Screen.Playlists.route } == true,
                onClick = {
                    navController.navigate(Screen.Playlists.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(8.dp)
            .wrapContentSize()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) AppSpotifyGreen else AppSpotifyLightGrey,
            modifier = Modifier.size(26.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = if (isSelected) AppSpotifyGreen else AppSpotifyLightGrey,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}