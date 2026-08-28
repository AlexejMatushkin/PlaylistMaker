package com.practicum.playlistmaker.ui.mediaLibrary.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.mediaLibrary.PlaylistsScreen
import com.practicum.playlistmaker.ui.mediaLibrary.viewModel.PlaylistsViewModel
import com.practicum.playlistmaker.ui.playlist.PlaylistFragment
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PlaylistMakerTheme {
                    PlaylistsScreen(
                        viewModel = viewModel,
                        onCreatePlaylist = {
                            findNavController().navigate(R.id.action_libraryFragment_to_createPlaylistFragment)
                        },
                        onPlaylistClick = { playlist ->
                            findNavController().navigate(
                                R.id.action_libraryFragment_to_playlistFragment,
                                bundleOf(PlaylistFragment.ARG_PLAYLIST_ID to playlist.id)
                            )
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylists()
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}