package com.example.p_music.domain.model

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val audioCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val audios: List<Audio> = emptyList()
)
