package com.practicum.playlistmaker.ui.mediaLibrary

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.common.components.Placeholder
import com.practicum.playlistmaker.ui.common.components.TrackCard
import com.practicum.playlistmaker.ui.mediaLibrary.viewModel.FavoriteTracksState
import com.practicum.playlistmaker.ui.mediaLibrary.viewModel.FavoriteTracksViewModel

@Composable
fun FavoriteTracksScreen(
    viewModel: FavoriteTracksViewModel,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.observeAsState()

    when (val current = state) {
        is FavoriteTracksState.Empty -> {
            Placeholder(
                imageRes = R.drawable.ic_placeholder_no_results,
                message = stringResource(R.string.empty_message_favorites)
            )
        }

        is FavoriteTracksState.Content -> {
            LazyColumn(
                modifier = modifier.fillMaxSize()
            ) {
                items(current.tracks, key = { it.trackId }) { track ->
                    TrackCard(
                        track = track,
                        onClick = { onTrackClick(track) }
                    )
                }
            }
        }

        null -> Unit
    }
}