package com.practicum.playlistmaker.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.common.components.Placeholder
import com.practicum.playlistmaker.ui.common.components.ScreenToolbar
import com.practicum.playlistmaker.ui.common.components.TrackCard
import com.practicum.playlistmaker.ui.search.view_model.SearchHistoryState
import com.practicum.playlistmaker.ui.search.view_model.SearchState
import com.practicum.playlistmaker.ui.search.view_model.SearchViewModel
import com.practicum.playlistmaker.ui.theme.BlackMain
import com.practicum.playlistmaker.ui.theme.Blue
import com.practicum.playlistmaker.ui.theme.Gray
import com.practicum.playlistmaker.ui.theme.LightGray
import com.practicum.playlistmaker.ui.theme.White
import com.practicum.playlistmaker.ui.theme.YsDisplayFontFamily

private const val MAX_QUERY_LENGTH = 15

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchState by viewModel.searchState.observeAsState()
    val historyState by viewModel.historyState.observeAsState()
    val query by viewModel.query.observeAsState("")
    val isDarkTheme = isSystemInDarkTheme()

    var hasFocus by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val showHistory = historyState is SearchHistoryState.History &&
        hasFocus &&
        query.isEmpty() &&
        searchState is SearchState.Empty

    val onTrackClicked: (Track) -> Unit = { track ->
        if (viewModel.clickDebounce()) {
            viewModel.addToHistory(track)
            onTrackClick(track)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .safeDrawingPadding()
    ) {
        ScreenToolbar(title = stringResource(R.string.search))

        SearchField(
            query = query,
            isDarkTheme = isDarkTheme,
            hasFocus = hasFocus,
            onFocusChanged = { focused ->
                hasFocus = focused
                viewModel.onFocusChanged(focused)
            },
            onQueryChanged = { text ->
                if (text.length <= MAX_QUERY_LENGTH) {
                    viewModel.updateQuery(text)
                }
            },
            onClear = {
                viewModel.clearQuery()
                keyboardController?.hide()
            },
            onSearchAction = {
                if (viewModel.query.value?.isNotBlank() == true) {
                    viewModel.searchImmediately(query)
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            }
        )

        if (showHistory) {
            val historyTracks = (historyState as SearchHistoryState.History).tracks
            SearchHistory(
                tracks = historyTracks,
                onTrackClick = onTrackClicked,
                onClearHistory = {
                    viewModel.clearHistory()
                    keyboardController?.hide()
                }
            )
        } else {
            SearchContent(
                state = searchState,
                onTrackClick = onTrackClicked,
                onRetry = { viewModel.retryLastSearch() }
            )
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    isDarkTheme: Boolean,
    hasFocus: Boolean,
    onFocusChanged: (Boolean) -> Unit,
    onQueryChanged: (String) -> Unit,
    onClear: () -> Unit,
    onSearchAction: () -> Unit
) {
    val containerColor = if (isDarkTheme) White else LightGray
    val hintColor = if (isDarkTheme) BlackMain else Gray

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 4.dp, bottom = 4.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier
                .fillMaxSize()
                .onFocusChanged { state -> onFocusChanged(state.isFocused) },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontFamily = YsDisplayFontFamily,
                fontSize = 16.sp,
                color = BlackMain
            ),
            cursorBrush = SolidColor(Blue),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSearchAction() }),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = hintColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search),
                                fontFamily = YsDisplayFontFamily,
                                fontSize = 16.sp,
                                color = hintColor
                            )
                        }
                        innerTextField()
                    }
                    if (query.isNotEmpty()) {
                        IconButton(onClick = onClear) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                tint = hintColor
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun SearchContent(
    state: SearchState?,
    onTrackClick: (Track) -> Unit,
    onRetry: () -> Unit
) {
    when (state) {
        is SearchState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        is SearchState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                items(state.tracks, key = { it.trackId }) { track ->
                    TrackCard(
                        track = track,
                        onClick = { onTrackClick(track) }
                    )
                }
            }
        }

        is SearchState.NoResults -> {
            Placeholder(
                imageRes = R.drawable.ic_placeholder_no_results,
                message = stringResource(R.string.nothing_found)
            )
        }

        is SearchState.Error -> {
            Placeholder(
                imageRes = R.drawable.ic_placeholder_error_track,
                message = stringResource(R.string.something_went_wrong),
                buttonText = stringResource(R.string.retry),
                onButtonClick = onRetry
            )
        }

        else -> Unit
    }
}

@Composable
private fun SearchHistory(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.you_searched),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tracks, key = { it.trackId }) { track ->
                TrackCard(
                    track = track,
                    onClick = { onTrackClick(track) }
                )
            }
        }
        Button(
            onClick = onClearHistory,
            shape = RoundedCornerShape(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp, bottom = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.clear_history),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}