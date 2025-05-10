package com.example.p_music.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_videos")
data class FavoriteVideoEntity(
    @PrimaryKey
    val videoId: Long,
    val title: String,
    val displayName: String,
    val duration: Long,
    val uri: String,
    val thumbnailUri: String?,
    val path: String,
    val size: Long,
    val dateAdded: Long,
    val dateFavorited: Long = System.currentTimeMillis()
) 