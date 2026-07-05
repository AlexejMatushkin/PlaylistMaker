package com.practicum.playlistmaker.ui.createPlaylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : CreatePlaylistFragment() {

    override val viewModel: EditPlaylistViewModel by viewModel()
    private var playlistId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        playlistId = arguments?.getInt(ARG_PLAYLIST_ID) ?: run {
            return super.onCreateView(inflater, container, savedInstanceState).also {
                findNavController().popBackStack()
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.menuButton.title = getString(R.string.edit_playlist)
        viewModel.loadPlaylist(playlistId)
    }

    override fun setupToolbar() {
        binding.menuButton.setNavigationOnClickListener {
            handleBackPress()
        }
    }

    override fun setupCreateButton() {
        binding.createButton.text = getString(R.string.save)
        binding.createButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            viewModel.createPlaylist(
                name = name,
                description = binding.descriptionEditText.text.toString()
            )
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_saved),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().popBackStack()
        }
    }

    override fun handleBackPress() {
        findNavController().popBackStack()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewModel.observeLoadedPlaylist().observe(viewLifecycleOwner) { playlist ->
            if (playlist != null) {
                binding.nameEditText.setText(playlist.name)
                binding.descriptionEditText.setText(playlist.description)
                if (playlist.imagePath.isNotEmpty()) {
                    Glide.with(this)
                        .load(playlist.imagePath)
                        .placeholder(R.drawable.ic_add_photo_312)
                        .transform(CenterCrop(), RoundedCorners(dpToPx(8f)))
                        .into(binding.pickerImage)
                }
            }
        }
    }

    companion object {
        const val ARG_PLAYLIST_ID = "playlistId"
    }
}
