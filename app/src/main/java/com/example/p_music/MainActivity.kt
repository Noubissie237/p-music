package com.example.p_music

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.p_music.presentation.ui.screens.MainScreen
import com.example.p_music.presentation.ui.theme.PMusicTheme
import com.example.p_music.service.AudioPlaybackService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        Log.d("MainActivity", "Permissions result: $permissions")
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            Log.d("MainActivity", "All permissions granted. Starting audio service.")
            // Démarrer le service seulement si les permissions sont accordées
            startAudioService()
        } else {
            Log.e("MainActivity", "Some permissions were denied: ${permissions.filter { !it.value }}")
            // TODO: Afficher un message à l'utilisateur
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Demander les permissions d'abord
        requestPermissions()
        
        setContent {
            PMusicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
    
    private fun requestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        Log.d("MainActivity", "Requesting permissions: ${permissions.joinToString()}")
        Log.d("MainActivity", "Android SDK version: ${Build.VERSION.SDK_INT}")
        requestPermissionLauncher.launch(permissions)
    }
    
    private fun startAudioService() {
        val intent = Intent(this, AudioPlaybackService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}