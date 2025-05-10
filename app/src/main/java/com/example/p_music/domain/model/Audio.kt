package com.example.p_music.domain.model

import android.net.Uri
import java.time.Duration

data class Audio(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Duration,
    val uri: Uri,
    val coverUri: Uri?,
    val path: String,
    val size: Long,
    val dateAdded: Long,
    val isFavorite: Boolean = false
) {
    companion object {
        val SUPPORTED_FORMATS = listOf(
            "mp3", "wav", "aac", "ogg", "m4a", "flac"
        )
    }
} 