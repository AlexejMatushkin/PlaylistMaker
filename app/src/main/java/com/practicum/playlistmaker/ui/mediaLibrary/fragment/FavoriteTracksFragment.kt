package com.practicum.playlistmaker.ui.mediaLibrary.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.mediaLibrary.viewModel.FavoriteTracksState
import com.practicum.playlistmaker.ui.mediaLibrary.viewModel.FavoriteTracksViewModel
import com.practicum.playlistmaker.ui.player.fragment.PlayerFragment
import com.practicum.playlistmaker.ui.search.adapter.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteTracksViewModel by viewModel()

    private lateinit var trackAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter()

        trackAdapter.setOnItemClickListener(object : TrackAdapter.OnItemClickListener {
            override fun onItemClick(track: Track) {
                val bundle = Bundle().apply {
                    putParcelable(PlayerFragment.EXTRA_TRACK, track)
                }
                findNavController().navigate(R.id.playerFragment, bundle)
            }
        })

        binding.rvFavoriteTracks.adapter = trackAdapter
        binding.rvFavoriteTracks.layoutManager = LinearLayoutManager(requireContext())

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteTracksState.Empty -> {
                    binding.rvFavoriteTracks.visibility = View.GONE
                    binding.emptyPlaceholder.visibility = View.VISIBLE
                }
                is FavoriteTracksState.Content -> {
                    binding.rvFavoriteTracks.visibility = View.VISIBLE
                    binding.emptyPlaceholder.visibility = View.GONE
                    trackAdapter.updateData(state.tracks)
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}