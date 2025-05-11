package com.example.p_music.presentation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Couleurs Spotify
private val SpotifyGreen = Color(0xFF1DB954)
private val SpotifyBlack = Color(0xFF191414)
private val SpotifyDarkGray = Color(0xFF282828)
private val SpotifyLightGray = Color(0xFFB3B3B3)
private val SpotifyWhite = Color(0xFFFFFFFF)

private val DarkColorScheme = darkColorScheme(
    primary = SpotifyColors.Green,
    secondary = SpotifyColors.LightGray,
    tertiary = SpotifyColors.DarkGray,
    background = SpotifyColors.Black,
    surface = SpotifyColors.DarkGray,
    error = SpotifyColors.Error,
    onPrimary = SpotifyColors.White,
    onSecondary = SpotifyColors.Black,
    onTertiary = SpotifyColors.White,
    onBackground = SpotifyColors.White,
    onSurface = SpotifyColors.White,
    onError = SpotifyColors.White
)

private val LightColorScheme = lightColorScheme(
    primary = SpotifyColors.Green,
    secondary = SpotifyColors.DarkGray,
    tertiary = SpotifyColors.LightGray,
    background = SpotifyColors.White,
    surface = SpotifyColors.LightGray,
    error = SpotifyColors.Error,
    onPrimary = SpotifyColors.White,
    onSecondary = SpotifyColors.White,
    onTertiary = SpotifyColors.Black,
    onBackground = SpotifyColors.Black,
    onSurface = SpotifyColors.Black,
    onError = SpotifyColors.White
)

@Composable
fun PMusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 