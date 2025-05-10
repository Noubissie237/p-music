package com.example.p_music.domain.usecase.video

import com.example.p_music.domain.model.Video
import com.example.p_music.domain.repository.VideoRepository
import javax.inject.Inject

class ToggleVideoFavoriteUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(video: Video) {
        repository.toggleFavorite(video)
    }
} 