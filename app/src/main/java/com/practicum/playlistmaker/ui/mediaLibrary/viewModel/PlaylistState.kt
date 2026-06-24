package com.practicum.playlistmaker.ui.mediaLibrary.viewModel

import com.practicum.playlistmaker.domain.playlist.model.Playlist

sealed interface PlaylistState {
    data class Content(val playlists: List<Playlist>) : PlaylistState
    data object Empty : PlaylistState
}
