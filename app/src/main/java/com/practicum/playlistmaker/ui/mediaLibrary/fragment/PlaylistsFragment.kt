package com.practicum.playlistmaker.ui.mediaLibrary.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.domain.playlist.model.Playlist
import com.practicum.playlistmaker.ui.mediaLibrary.adapter.PlaylistAdapter
import com.practicum.playlistmaker.ui.mediaLibrary.viewModel.PlaylistState
import com.practicum.playlistmaker.ui.mediaLibrary.viewModel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()
    private var adapter: PlaylistAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_createPlaylistFragment)
        }

        setupRecyclerView()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylists()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> showContent(state.playlists)
            is PlaylistState.Empty -> showEmpty()
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.emptyLibrary.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        adapter = PlaylistAdapter(playlists) { playlist ->
            findNavController().navigate(
                R.id.action_libraryFragment_to_playlistFragment,
                bundleOf("playlistId" to playlist.id)
            )
        }
        binding.recyclerView.adapter = adapter
    }

    private fun showEmpty() {
        binding.emptyLibrary.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}
