package com.example.p_music.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.p_music.R

sealed class Screen(
    val route: String,
    val titleResId: Int
) {
    object Music : Screen("music", R.string.music)
    object Videos : Screen("videos", R.string.videos)
    object Favorites : Screen("favorites", R.string.favorites)
    object Playlists : Screen("playlists", R.string.playlists)
    object Settings : Screen("settings", R.string.settings)
    object AudioPlayer : Screen("audio_player/{audioId}", R.string.audio_player) {
        fun createRoute(audioId: String) = "audio_player/$audioId"
        
        val arguments = listOf(
            navArgument("audioId") {
                type = NavType.StringType
            }
        )
    }
    
    object PlaylistDetail : Screen("playlist_detail/{playlistId}", R.string.playlists) {
        fun createRoute(playlistId: Long) = "playlist_detail/$playlistId"
        
        val arguments = listOf(
            navArgument("playlistId") {
                type = NavType.LongType
            }
        )
    }
} 