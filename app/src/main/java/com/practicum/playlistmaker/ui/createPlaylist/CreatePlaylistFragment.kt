package com.practicum.playlistmaker.ui.createPlaylist

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreatePlaylistViewModel by viewModel()

    private var savedImagePath: String? = null
    private var hasUnsavedData = false

    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            hasUnsavedData = true
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.ic_add_photo_312)
                .transform(CenterCrop(), RoundedCorners(dpToPx(8f)))
                .into(binding.pickerImage)
            savedImagePath = copyImageToPrivateStorage(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupImagePicker()
        setupTextWatcher()
        setupCreateButton()
        setupBackHandler()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.menuButton.setNavigationOnClickListener {
            handleBackPress()
        }
    }

    private fun setupImagePicker() {
        binding.pickerImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setupTextWatcher() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onNameChanged(s?.toString() ?: "")
                hasUnsavedData = true
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.nameEditText.addTextChangedListener(watcher)

        val descWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                hasUnsavedData = true
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.descriptionEditText.addTextChangedListener(descWatcher)
    }

    private fun setupCreateButton() {
        binding.createButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            viewModel.createPlaylist(
                name = name,
                description = binding.descriptionEditText.text.toString(),
                imagePath = savedImagePath ?: ""
            )
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_created, name),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().popBackStack()
        }
    }

    private fun setupBackHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPress()
                }
            }
        )
    }

    private fun handleBackPress() {
        if (hasUnsavedData) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.finish_create_pl)
                .setMessage(R.string.finish_create_pl_msg)
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.finish) { _, _ ->
                    findNavController().popBackStack()
                }
                .show()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.observeNameState().observe(viewLifecycleOwner) { state ->
            binding.createButton.isEnabled = !state.isEmpty
        }
    }

    private fun copyImageToPrivateStorage(uri: Uri): String {
        val dir = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlist_covers"
        )
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "cover_${System.currentTimeMillis()}.jpg")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory.decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        outputStream.close()
        inputStream?.close()
        return file.absolutePath
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
