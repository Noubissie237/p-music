package com.example.p_music.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.p_music.data.repository.AudioRepository
import com.example.p_music.domain.model.Audio

@Composable
fun MusicScreen() {
    val context = LocalContext.current
    val audioRepository = remember { AudioRepository(context) }
    var audioList by remember { mutableStateOf<List<Audio>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        audioList = audioRepository.getAllAudios()
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(audioList) { audio ->
            AudioItem(audio = audio)
        }
    }
}

@Composable
fun AudioItem(audio: Audio) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = audio.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = audio.artist,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
} 