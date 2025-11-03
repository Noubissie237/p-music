package com.example.p_music.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistWithAudioIds(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "playlistId"
    )
    val audioRefs: List<PlaylistAudioCrossRef> = emptyList()
)
