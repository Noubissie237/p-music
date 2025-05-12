package com.example.p_music.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val musicViewModel: MusicViewModel = hiltViewModel()
    val audioPlayerViewModel: AudioPlayerViewModel = hiltViewModel()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination

    // Déterminer si nous sommes sur la page de lecture
    val isPlayerScreen = currentDestination?.route?.startsWith("player/") == true

    Scaffold(

        bottomBar = {
            if (!isPlayerScreen) {
                NavigationBar {
                    val navBackStackEntry = navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry.value?.destination

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.MusicNote, contentDescription = null) },
                        label = { Text(stringResource(R.string.music)) },
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.Music.route } == true,
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
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.VideoLibrary, contentDescription = null) },
                        label = { Text(stringResource(R.string.videos)) },
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.Videos.route } == true,
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
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                        label = { Text(stringResource(R.string.favorites)) },
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.Favorites.route } == true,
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
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.PlaylistPlay, contentDescription = null) },
                        label = { Text(stringResource(R.string.playlists)) },
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.Playlists.route } == true,
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
                    PlaylistsScreen()
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

            // Mini player toujours visible sauf sur la page de lecture
            if (!isPlayerScreen) {
                val uiState by audioPlayerViewModel.uiState.collectAsState()
                if (uiState.currentAudio != null) {
                    MiniPlayer(
                        modifier = Modifier.align(Alignment.BottomCenter),
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