package com.example.p_music.presentation.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.p_music.domain.model.Video
import com.example.p_music.presentation.viewmodel.VideosViewModel
import java.io.File
import java.util.concurrent.TimeUnit

// Couleurs inspirées de Spotify
private val SpotifyBlack = Color(0xFF121212)
private val SpotifyDarkGray = Color(0xFF212121)
private val SpotifyGreen = Color(0xFF1DB954)
private val SpotifyLightGray = Color(0xFFB3B3B3)
private val SpotifyWhite = Color(0xFFFFFFFF)

@Composable
fun VideosScreen(
    viewModel: VideosViewModel = hiltViewModel(),
    onVideoClick: (Video) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SpotifyBlack)
            .padding(16.dp)
    ) {
        Text(
            text = "Vidéos",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = SpotifyWhite
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            uiState.isLoading -> LoadingView()
            uiState.error != null -> ErrorView(uiState.error)
            uiState.videos.isEmpty() -> EmptyView()
            else -> VideosList(uiState.videos, onVideoClick)
        }
    }
}

@Composable
private fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = SpotifyGreen)
    }
}

@Composable
private fun ErrorView(error: String?) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Erreur de chargement", color = SpotifyWhite)
            Spacer(modifier = Modifier.height(8.dp))
            Text("$error", color = SpotifyLightGray)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* Refresh */ }, colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen)) {
                Text("Réessayer")
            }
        }
    }
}

@Composable
private fun EmptyView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Aucune vidéo disponible", color = SpotifyLightGray)
    }
}

@Composable
private fun VideosList(videos: List<Video>, onVideoClick: (Video) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(videos) { video ->
            VideoRowItem(video, onVideoClick = { onVideoClick(video) })
        }
    }
}

@Composable
private fun VideoRowItem(video: Video, onVideoClick: () -> Unit, viewModel: VideosViewModel = hiltViewModel(), modifier: Modifier = Modifier) {
    var showMenu by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Déclenche le chargement en arrière-plan dès que la vidéo est visible
    LaunchedEffect(video.id) {
        viewModel.loadThumbnail(video, context)
    }

    // Observe le cache du ViewModel
    val bitmap = viewModel.thumbnailCache[video.id]

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SpotifyDarkGray)
            .clickable(onClick = onVideoClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VideoThumbnailSmart(bitmap, video.title, video.duration.toMillis())

        // Reste inchangé
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(video.title, color = SpotifyWhite, maxLines = 2)
            Spacer(modifier = Modifier.height(4.dp))
            Text(formatFileSize(video.size), color = SpotifyLightGray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(File(video.path).parentFile?.name ?: "Unknown", color = SpotifyLightGray.copy(alpha = 0.7f))
        }

        IconButton(onClick = { showBottomSheet = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = SpotifyLightGray)
        }

        if (showBottomSheet) {
            VideoOptionsBottomSheet(
                video = video,
                onDismiss = { showBottomSheet = false },
                onConvertToMp3 = { /* Implémentation à venir */ },
                onToggleFavorite = { /* Implémentation à venir */ },
                onRename = { /* Implémentation à venir */ },
                onDelete = { /* Implémentation à venir */ },
                onShare = { /* Implémentation à venir */ },
                onShowInfo = { /* Implémentation à venir */ }
            )
        }
    }
}


@Composable
private fun VideoThumbnailSmart(bitmap: Bitmap?, title: String?, durationMs: Long) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .fillMaxHeight()
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            DefaultThumbnailFallback()
        }

        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(formatDuration(durationMs), style = MaterialTheme.typography.bodySmall, color = SpotifyWhite)
        }
    }
}


@Composable
private fun DefaultThumbnailFallback() {
    Box(Modifier.fillMaxSize().background(Color.DarkGray), contentAlignment = Alignment.Center) {
        Icon(Icons.Default.PlayCircleFilled, contentDescription = "Video", tint = SpotifyGreen, modifier = Modifier.size(48.dp))
    }
}

fun extractThumbnail(context: Context, videoUri: Uri): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        context.contentResolver.openFileDescriptor(videoUri, "r")?.use { pfd ->
            retriever.setDataSource(pfd.fileDescriptor)
            val bitmap = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            retriever.release()
            bitmap
        }
    } catch (e: Exception) {
        Log.e("VideoThumbnail", "Extraction failed: ${e.message}")
        null
    }
}

fun formatFileSize(sizeInBytes: Long): String {
    return when {
        sizeInBytes < 1024 -> "$sizeInBytes B"
        sizeInBytes < 1024 * 1024 -> "${sizeInBytes / 1024} KB"
        sizeInBytes < 1024 * 1024 * 1024 -> "${sizeInBytes / (1024 * 1024)} MB"
        else -> "${sizeInBytes / (1024 * 1024 * 1024)} GB"
    }
}

public fun formatDuration(durationMs: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
