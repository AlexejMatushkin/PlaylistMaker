package com.practicum.playlistmaker.ui.mediaLibrary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.playlist.model.Playlist
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.common.components.ScreenToolbar
import com.practicum.playlistmaker.ui.mediaLibrary.viewModel.FavoriteTracksViewModel
import com.practicum.playlistmaker.ui.mediaLibrary.viewModel.PlaylistsViewModel

@Composable
fun MediaLibraryScreen(
    favoriteTracksViewModel: FavoriteTracksViewModel,
    playlistsViewModel: PlaylistsViewModel,
    onCreatePlaylist: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .safeDrawingPadding()
    ) {
        ScreenToolbar(title = stringResource(R.string.media_library))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MaterialTheme.colorScheme.onSurface,
                    height = 2.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = stringResource(R.string.favorite_tracks),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = stringResource(R.string.playlists),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }

        when (selectedTab) {
            0 -> FavoriteTracksScreen(
                viewModel = favoriteTracksViewModel,
                onTrackClick = onTrackClick
            )

            else -> PlaylistsScreen(
                viewModel = playlistsViewModel,
                onCreatePlaylist = onCreatePlaylist,
                onPlaylistClick = onPlaylistClick
            )
        }
    }
}