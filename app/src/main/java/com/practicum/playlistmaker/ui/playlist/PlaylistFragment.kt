package com.practicum.playlistmaker.ui.playlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.player.fragment.PlayerFragment
import com.practicum.playlistmaker.ui.search.adapter.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by viewModel()
    private var trackAdapter: TrackAdapter? = null
    private var playlistMenuAdapter: PlaylistMenuAdapter? = null
    private var playlistId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
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

        playlistId = arguments?.getInt(ARG_PLAYLIST_ID) ?: run {
            findNavController().popBackStack()
            return
        }

        setupBackButton()
        setupShareButton()
        setupMenuButton()
        setupTrackBottomSheet()
        setupMenuBottomSheet()
        viewModel.loadPlaylist(playlistId)
        observeViewModel()
    }

    private fun setupBackButton() {
        binding.menuButton.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupShareButton() {
        binding.share.setOnClickListener {
            val shareText = viewModel.getShareText()
            if (shareText != null) {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.share_playlist_empty,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupMenuButton() {
        binding.menu.setOnClickListener {
            BottomSheetBehavior.from(binding.menuBottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setupTrackBottomSheet() {
        BottomSheetBehavior.from(binding.trackBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        trackAdapter = TrackAdapter().apply {
            setOnItemClickListener(object : TrackAdapter.OnItemClickListener {
                override fun onItemClick(track: Track) {
                    val bundle = Bundle().apply {
                        putParcelable(PlayerFragment.EXTRA_TRACK, track)
                    }
                    findNavController().navigate(R.id.action_playlistFragment_to_playerFragment, bundle)
                }
            })
            setOnItemLongClickListener(object : TrackAdapter.OnItemLongClickListener {
                override fun onItemLongClick(track: Track): Boolean {
                    showDeleteTrackDialog(track)
                    return true
                }
            })
        }

        binding.trackRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.trackRecycler.adapter = trackAdapter
    }

    private fun setupMenuBottomSheet() {
        val menuBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.overlay.setOnClickListener {
            menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (_binding == null) return
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                        binding.trackBottomSheet.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                        binding.trackBottomSheet.visibility = View.GONE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (_binding == null) return
                binding.overlay.alpha = if (slideOffset > 0) slideOffset else 0f
            }
        })

        binding.shareTextView.setOnClickListener {
            menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.share.performClick()
        }

        binding.editTextView.setOnClickListener {
            menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val bundle = Bundle().apply {
                putInt(ARG_PLAYLIST_ID, playlistId)
            }
            findNavController().navigate(R.id.action_playlistFragment_to_editPlaylistFragment, bundle)
        }

        binding.deleteTextView.setOnClickListener {
            menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }

        playlistMenuAdapter = PlaylistMenuAdapter()
        binding.playlist.layoutManager = LinearLayoutManager(requireContext())
        binding.playlist.adapter = playlistMenuAdapter
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.WhiteAlertDialogTheme)
            .setTitle(R.string.delete_track_title)
            .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.removeTrack(playlistId, track.trackId)
            }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        val playlistName = viewModel.state.value?.playlist?.name ?: ""
        MaterialAlertDialogBuilder(requireContext(), R.style.WhiteAlertDialogTheme)
            .setTitle(getString(R.string.delete_playlist_message, playlistName))
            .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deletePlaylist()
                findNavController().popBackStack()
            }
            .show()
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }

    }

    private fun render(state: PlaylistState) {
        val playlist = state.playlist ?: return

        binding.name.text = playlist.name

        if (playlist.description.isNotEmpty()) {
            binding.description.visibility = View.VISIBLE
            binding.description.text = playlist.description
        } else {
            binding.description.visibility = View.GONE
        }

        binding.time.text = state.totalDuration

        val trackCount = playlist.count
        binding.count.text = resources.getQuantityString(
            R.plurals.track_count,
            trackCount,
            trackCount
        )

        val placeholder = R.drawable.ic_placeholder_360
        Glide.with(requireContext())
            .load(playlist.imagePath.ifEmpty { null })
            .placeholder(placeholder)
            .error(placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(dpToPx(8f))
            )
            .into(binding.image)

        if (state.tracks.isEmpty()) {
            Toast.makeText(requireContext(), R.string.empty_playlist_mes, Toast.LENGTH_SHORT).show()
        }
        trackAdapter?.updateData(state.tracks)
        playlistMenuAdapter?.setPlaylist(playlist)
    }

    private fun dpToPx(dp: Float): Int {
        return android.util.TypedValue.applyDimension(
            android.util.TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_PLAYLIST_ID = "playlistId"
    }
}
