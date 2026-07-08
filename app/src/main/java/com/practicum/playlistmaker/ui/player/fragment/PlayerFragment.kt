package com.practicum.playlistmaker.ui.player.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.domain.playlist.model.Playlist
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.mediaLibrary.adapter.PlaylistPlayerAdapter
import com.practicum.playlistmaker.ui.player.view_model.AddTrackResult
import com.practicum.playlistmaker.ui.player.view_model.PlayerState
import com.practicum.playlistmaker.ui.player.view_model.PlaylistSheetState
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModel()

    private var playerAdapter: PlaylistPlayerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(EXTRA_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(EXTRA_TRACK)
        } ?: run {
            findNavController().popBackStack()
            return
        }

        viewModel.loadTrack(track)

        setupViews(track)
        setupBackButton()
        setupPlayButton()
        setupFavoriteButton()
        setupAddToPlaylistButton()
        viewModel.hideSheet()
        setupBottomSheet()
        observeViewModel()
    }

    private fun setupViews(track: Track) = binding.apply {
        trackName.text = track.trackName
        artistName.text = track.artistName
        genre.text = track.primaryGenreName ?: ""
        country.text = track.country ?: ""
        trackTime.text = track.getFormattedTime()
        trackProgress.text = getString(R.string.track_time_default)

        val placeholder = R.drawable.ic_music_note
        val highResUrl = track.getCoverArtwork()
        if (highResUrl != null) {
            Glide.with(requireContext())
                .load(highResUrl)
                .placeholder(placeholder)
                .error(placeholder)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(albumCover)
        } else {
            albumCover.setImageResource(placeholder)
        }

        if (!track.collectionName.isNullOrEmpty()) {
            albumName.text = track.collectionName
            albumLabel.isVisible = true
            albumName.isVisible = true
        } else {
            albumLabel.isVisible = false
            albumName.isVisible = false
        }

        val year = track.releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4)
        if (year != null) {
            releaseYear.text = year
            yearLabel.isVisible = true
            releaseYear.isVisible = true
        } else {
            yearLabel.isVisible = false
            releaseYear.isVisible = false
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            viewModel.releasePlayer()
            findNavController().popBackStack()
        }
    }

    private fun setupPlayButton() {
        binding.playButton.onPlayButtonClickListener = {
            viewModel.togglePlay()
        }
    }

    private fun setupFavoriteButton() {
        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }
    }

    private fun setupAddToPlaylistButton() {
        binding.addToPlaylistButton.setOnClickListener {
            viewModel.showSheet()
        }
    }

    private fun setupBottomSheet() {
        binding.overlay.visibility = View.GONE

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.overlay.setOnClickListener {
            val behavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = if (slideOffset > 0) slideOffset else 0f
            }
        })

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_createPlaylistFragment)
        }

        playerAdapter = PlaylistPlayerAdapter(emptyList()) { playlist ->
            viewModel.addTrackToPlaylist(playlist)
        }
        binding.playlistSheetRecycler.adapter = playerAdapter
        binding.playlistSheetRecycler.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() = binding.apply {
        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            when (state.playerState) {
                PlayerState.Playing -> {
                    playButton.setPlayingState(true)
                    playButton.isEnabled = true
                }
                PlayerState.Prepared, PlayerState.Paused -> {
                    playButton.setPlayingState(false)
                    playButton.isEnabled = true
                }
                PlayerState.Default -> {
                    playButton.setPlayingState(false)
                    playButton.isEnabled = true
                }
                PlayerState.Error -> {
                    playButton.isEnabled = false
                }
            }
            trackProgress.text = viewModel.getFormattedTime(state.currentPosition)
            favoriteButton.setImageResource(
                if (state.isFavorite) R.drawable.ic_favorite_51 else R.drawable.ic_favorite_border
            )
        }

        viewModel.playlistState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistSheetState.Hidden -> {
                    val behavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)
                    behavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
                is PlaylistSheetState.Loading -> {}
                is PlaylistSheetState.Empty -> {
                    playerAdapter = PlaylistPlayerAdapter(emptyList()) { }
                    binding.playlistSheetRecycler.adapter = playerAdapter
                    val behavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
                is PlaylistSheetState.Content -> {
                    playerAdapter = PlaylistPlayerAdapter(state.playlists) { playlist ->
                        viewModel.addTrackToPlaylist(playlist)
                    }
                    binding.playlistSheetRecycler.adapter = playerAdapter
                    val behavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }

        viewModel.addTrackResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AddTrackResult.Added -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.added_to_playlist, result.playlistName),
                        Toast.LENGTH_SHORT
                    ).show()
                    val behavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)
                    behavior.state = BottomSheetBehavior.STATE_HIDDEN
                    viewModel.clearAddTrackResult()
                }
                is AddTrackResult.AlreadyInPlaylist -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.already_in_playlist, result.playlistName),
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.clearAddTrackResult()
                }
                null -> {}
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroyView() {
        viewModel.releasePlayer()
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val EXTRA_TRACK = "extra_track"
    }
}
