package com.example.p_music.domain.model

import android.net.Uri

data class Audio(
    val id: String,
    val title: String,
    val artist: String,
    val duration: Long,
    val path: String,
    val album: String,
    val uri: Uri,
    val coverUri: Uri?,
    val size: Long,
    val dateAdded: Long,
    val isFavorite: Boolean = false
) {
    companion object {
        val SUPPORTED_FORMATS = listOf(
            "mp3", "wav", "ogg", "m4a", "flac", "aac"
        )
    }
} 