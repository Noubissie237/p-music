package com.example.p_music.domain.usecase.favorite

import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.repository.FavoriteAudioRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: FavoriteAudioRepository
) {
    suspend operator fun invoke(audio: Audio) {
        repository.toggleFavorite(audio)
    }
} 