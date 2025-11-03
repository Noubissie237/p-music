package com.example.p_music.data.local.dao

import androidx.room.*
import com.example.p_music.data.local.entity.PlaylistAudioCrossRef
import com.example.p_music.data.local.entity.PlaylistEntity
import com.example.p_music.data.local.entity.PlaylistWithAudioIds
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    
    @Query("SELECT * FROM playlists ORDER BY updatedAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
    
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity?
    
    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    fun getPlaylistWithAudioIds(playlistId: Long): Flow<PlaylistWithAudioIds?>
    
    @Transaction
    @Query("SELECT * FROM playlists ORDER BY updatedAt DESC")
    fun getAllPlaylistsWithAudioIds(): Flow<List<PlaylistWithAudioIds>>
    
    @Query("SELECT audioId FROM playlist_audio_cross_ref WHERE playlistId = :playlistId ORDER BY position")
    suspend fun getAudioIdsForPlaylist(playlistId: Long): List<String>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long
    
    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)
    
    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAudioToPlaylist(crossRef: PlaylistAudioCrossRef)
    
    @Delete
    suspend fun removeAudioFromPlaylist(crossRef: PlaylistAudioCrossRef)
    
    @Query("DELETE FROM playlist_audio_cross_ref WHERE playlistId = :playlistId AND audioId = :audioId")
    suspend fun removeAudioFromPlaylistById(playlistId: Long, audioId: String)
    
    @Query("SELECT COUNT(*) FROM playlist_audio_cross_ref WHERE playlistId = :playlistId AND audioId = :audioId")
    suspend fun isAudioInPlaylist(playlistId: Long, audioId: String): Int
    
    @Query("SELECT COUNT(*) FROM playlist_audio_cross_ref WHERE playlistId = :playlistId")
    suspend fun getPlaylistAudioCount(playlistId: Long): Int
}
