package com.example.p_music.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p_music.domain.model.Audio
import com.example.p_music.domain.model.Playlist
import com.example.p_music.domain.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    val playlistRepository: PlaylistRepository
) : ViewModel() {

    val playlists: StateFlow<List<Playlist>> = playlistRepository.getAllPlaylists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createPlaylist(name: String, description: String = "") {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name, description)
        }
    }

    fun addAudioToPlaylist(playlistId: Long, audio: Audio) {
        viewModelScope.launch {
            playlistRepository.addAudioToPlaylist(playlistId, audio)
        }
    }

    fun removeAudioFromPlaylist(playlistId: Long, audioId: String) {
        viewModelScope.launch {
            playlistRepository.removeAudioFromPlaylist(playlistId, audioId)
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlist)
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistRepository.updatePlaylist(playlist)
        }
    }
}
