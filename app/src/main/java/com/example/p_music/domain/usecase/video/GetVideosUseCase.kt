package com.example.p_music.domain.usecase.video

import com.example.p_music.domain.model.Video
import com.example.p_music.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVideosUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    operator fun invoke(): Flow<List<Video>> {
        return repository.getAllVideos()
    }
} 