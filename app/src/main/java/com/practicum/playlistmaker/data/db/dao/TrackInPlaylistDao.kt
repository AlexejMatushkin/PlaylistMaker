package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.db.entity.TrackInPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackInPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TrackInPlaylistEntity)

    @Query("SELECT * FROM tracks_in_playlist")
    fun getAllTracks(): Flow<List<TrackInPlaylistEntity>>

    @Query("DELETE FROM tracks_in_playlist WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Long)
}
