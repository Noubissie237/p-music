package com.example.p_music.domain.model

import android.net.Uri

data class Audio(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long, // en millisecondes
    val uri: Uri,
    val path: String,
    val coverUri: Uri? = null,
    val size: Long = 0,
    val dateAdded: Long = 0,
    val isFavorite: Boolean = false
) {
    companion object {
        val SUPPORTED_FORMATS = listOf(
            "mp3", "wav", "aac", "ogg", "m4a", "flac"
        )
    }
} 