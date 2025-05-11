package com.example.p_music.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.p_music.R
import com.example.p_music.domain.model.Audio
import com.example.p_music.presentation.navigation.Screen
import com.example.p_music.presentation.ui.theme.PMusicTheme
import com.example.p_music.presentation.ui.theme.SpotifyDarkGray
import com.example.p_music.presentation.ui.theme.SpotifyGreen
import com.example.p_music.presentation.ui.theme.SpotifyLightGray
import com.example.p_music.presentation.viewmodel.AudioPlayerViewModel
import com.example.p_music.presentation.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Music,
        Screen.Videos,
        Screen.Favorites,
        Screen.Playlists,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = when (screen) {
                                    Screen.Music -> Icons.Default.Home
                                    Screen.Videos -> Icons.Default.VideoLibrary
                                    Screen.Favorites -> Icons.Default.Favorite
                                    Screen.Playlists -> Icons.Default.List
                                    Screen.Settings -> Icons.Default.Settings
                                    else -> Icons.Default.Home
                                },
                                contentDescription = null
                            )
                        },
                        label = { Text(stringResource(screen.titleResId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Music.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Music.route) {
                MusicScreen(
                    onAudioClick = { audio ->
                        navController.navigate(Screen.AudioPlayer.createRoute(audio.id))
                    }
                )
            }
            composable(Screen.Videos.route) {
                VideosScreen()
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
            composable(
                route = Screen.AudioPlayer.route,
                arguments = listOf(navArgument("audioId") { type = NavType.StringType })
            ) {
                val viewModel: AudioPlayerViewModel = hiltViewModel()
                val audioId = it.arguments?.getString("audioId") ?: ""
                LaunchedEffect(audioId) {
                    viewModel.loadAudio(audioId)
                }
                AudioPlayerScreen(viewModel = viewModel)
            }
        }
    }
} 