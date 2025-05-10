package com.example.p_music.presentation.navigation

sealed class Screen(val route: String, val icon: Int, val label: String) {
    object Music : Screen("music", android.R.drawable.ic_media_play, "Musique")
    object Videos : Screen("videos", android.R.drawable.ic_menu_gallery, "Vidéos")
    object Favorites : Screen("favorites", android.R.drawable.btn_star_big_on, "Favoris")
    object Playlists : Screen("playlists", android.R.drawable.ic_menu_sort_by_size, "Playlists")
    object Settings : Screen("settings", android.R.drawable.ic_menu_manage, "Paramètres")

    companion object {
        val bottomNavItems = listOf(Music, Videos, Favorites, Playlists, Settings)
    }
} 