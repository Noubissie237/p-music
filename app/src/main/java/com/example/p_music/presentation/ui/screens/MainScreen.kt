package com.example.p_music.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.p_music.presentation.navigation.Screen
import com.example.p_music.presentation.theme.PMusicTheme

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
                    // TODO: Implémenter l'écran de musique
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("Écran Musique")
                    }
                }
                composable(Screen.Videos.route) {
                    // TODO: Implémenter l'écran de vidéos
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("Écran Vidéos")
                    }
                }
                composable(Screen.Favorites.route) {
                    // TODO: Implémenter l'écran des favoris
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("Écran Favoris")
                    }
                }
                composable(Screen.Playlists.route) {
                    // TODO: Implémenter l'écran des playlists
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("Écran Playlists")
                    }
                }
                composable(Screen.Settings.route) {
                    // TODO: Implémenter l'écran des paramètres
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("Écran Paramètres")
                    }
                }
            }
        }
    }
} 