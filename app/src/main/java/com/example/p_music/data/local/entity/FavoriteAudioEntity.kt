package com.example.p_music.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_audios")
data class FavoriteAudioEntity(
    @PrimaryKey
    val audioId: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: String,
    val coverUri: String?,
    val path: String,
    val size: Long,
    val dateAdded: Long,
    val dateFavorited: Long = System.currentTimeMillis()
) 