package com.example.p_music.domain.usecase

import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.repository.AudioRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAudiosUseCase @Inject constructor(
    private val repository: AudioRepository
) {
    operator fun invoke(): Flow<List<Audio>> = repository.getAllAudios()
} 