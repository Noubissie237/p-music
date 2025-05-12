package com.example.p_music.domain.model

import android.net.Uri

data class Video(
    val id: Long,
    val title: String,
    val displayName: String,
    val duration: java.time.Duration,
    val uri: Uri,
    val thumbnailUri: Uri?,
    val path: String,
    val size: Long,
    val dateAdded: Long,
    val isFavorite: Boolean
) 