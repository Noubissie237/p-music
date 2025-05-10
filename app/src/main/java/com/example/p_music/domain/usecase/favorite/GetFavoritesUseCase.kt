package com.example.p_music.domain.usecase.favorite

import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.repository.FavoriteAudioRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val repository: FavoriteAudioRepository
) {
    operator fun invoke(): Flow<List<Audio>> {
        return repository.getAllFavorites()
    }
} 