package com.example.p_music.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.p_music.R
import com.example.p_music.presentation.navigation.Screen
import com.example.p_music.presentation.viewmodel.AudioPlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentRoute) {
                            Screen.Music.route -> stringResource(R.string.music)
                            Screen.Videos.route -> stringResource(R.string.videos)
                            Screen.Favorites.route -> stringResource(R.string.favorites)
                            Screen.Playlists.route -> stringResource(R.string.playlists)
                            Screen.Settings.route -> stringResource(R.string.settings)
                            else -> stringResource(R.string.app_name)
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.MusicNote, contentDescription = null) },
                    label = { Text(stringResource(R.string.music)) },
                    selected = currentRoute == Screen.Music.route,
                    onClick = { navController.navigate(Screen.Music.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.VideoLibrary, contentDescription = null) },
                    label = { Text(stringResource(R.string.videos)) },
                    selected = currentRoute == Screen.Videos.route,
                    onClick = { navController.navigate(Screen.Videos.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text(stringResource(R.string.favorites)) },
                    selected = currentRoute == Screen.Favorites.route,
                    onClick = { navController.navigate(Screen.Favorites.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.PlaylistPlay, contentDescription = null) },
                    label = { Text(stringResource(R.string.playlists)) },
                    selected = currentRoute == Screen.Playlists.route,
                    onClick = { navController.navigate(Screen.Playlists.route) }
                )
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
                arguments = Screen.AudioPlayer.arguments
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