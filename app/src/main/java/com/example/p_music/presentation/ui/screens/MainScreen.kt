package com.example.p_music.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.p_music.R
import com.example.p_music.domain.model.Audio
import com.example.p_music.presentation.navigation.Screen
import com.example.p_music.presentation.ui.theme.PMusicTheme
import com.example.p_music.presentation.ui.theme.SpotifyDarkGray
import com.example.p_music.presentation.ui.theme.SpotifyGreen
import com.example.p_music.presentation.ui.theme.SpotifyLightGray
import com.example.p_music.presentation.viewmodel.MusicViewModel
import com.example.p_music.presentation.ui.screens.MusicScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    PMusicTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    Screen.bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = when (screen) {
                                        Screen.Music -> Icons.Default.MusicNote
                                        Screen.Videos -> Icons.Default.VideoLibrary
                                        Screen.Favorites -> Icons.Default.Favorite
                                        Screen.Playlists -> Icons.Default.PlaylistPlay
                                        Screen.Settings -> Icons.Default.Settings
                                    },
                                    contentDescription = screen.label
                                )
                            },
                            label = { Text(screen.label) },
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
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Music.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.Music.route) {
                    MusicScreen()
                }
                composable(Screen.Videos.route) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("Écran Vidéos")
                    }
                }
                composable(Screen.Favorites.route) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("Écran Favoris")
                    }
                }
                composable(Screen.Playlists.route) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("Écran Playlists")
                    }
                }
                composable(Screen.Settings.route) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("Écran Paramètres")
                    }
                }
            }
        }
    }
} 