package com.example.p_music.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "playlist_audio_cross_ref",
    primaryKeys = ["playlistId", "audioId"],
    indices = [Index("audioId")],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaylistAudioCrossRef(
    val playlistId: Long,
    val audioId: String,
    val addedAt: Long = System.currentTimeMillis(),
    val position: Int = 0
)
