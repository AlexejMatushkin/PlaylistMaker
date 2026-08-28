package com.practicum.playlistmaker.ui.mediaLibrary.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.mediaLibrary.FavoriteTracksScreen
import com.practicum.playlistmaker.ui.mediaLibrary.viewModel.FavoriteTracksViewModel
import com.practicum.playlistmaker.ui.player.fragment.PlayerFragment
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private val viewModel: FavoriteTracksViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PlaylistMakerTheme {
                    FavoriteTracksScreen(
                        viewModel = viewModel,
                        onTrackClick = { track ->
                            val bundle = Bundle().apply {
                                putParcelable(PlayerFragment.EXTRA_TRACK, track)
                            }
                            findNavController().navigate(R.id.playerFragment, bundle)
                        }
                    )
                }
            }
        }
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}