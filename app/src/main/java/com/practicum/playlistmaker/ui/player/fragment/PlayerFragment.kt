package com.practicum.playlistmaker.ui.player.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.player.view_model.PlayerState
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModel()

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
        binding.playButton.setOnClickListener {
            viewModel.togglePlay()
        }
    }

    private fun observeViewModel() = binding.apply {
        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                PlayerState.Playing -> {
                    playButton.setImageResource(R.drawable.ic_pause)
                    playButton.isEnabled = true
                }
                PlayerState.Prepared, PlayerState.Paused -> {
                    playButton.setImageResource(R.drawable.ic_play_circle)
                    playButton.isEnabled = true
                }
                PlayerState.Default -> {
                    playButton.setImageResource(R.drawable.ic_play_circle)
                    playButton.isEnabled = true
                }
                PlayerState.Error -> {
                    playButton.isEnabled = false
                }
            }
        }

        viewModel.currentPosition.observe(viewLifecycleOwner) { position ->
            trackProgress.text = viewModel.getFormattedTime(position)
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
