package com.practicum.playlistmaker.ui.createPlaylist

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

open class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    protected val binding get() = _binding!!

    protected open val viewModel: CreatePlaylistViewModel by viewModel()

    private var hasUnsavedData = false

    protected val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            hasUnsavedData = true
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.ic_add_photo_312)
                .transform(CenterCrop(), RoundedCorners(dpToPx(8f)))
                .into(binding.pickerImage)
            viewModel.saveImage(uri.toString())
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
        setupKeyboardActions()
        setupCreateButton()
        setupBackHandler()
        observeViewModel()
    }

    protected open fun setupToolbar() {
        binding.menuButton.setNavigationOnClickListener {
            handleBackPress()
        }
    }

    protected open fun setupImagePicker() {
        binding.pickerImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setupTextWatcher() {
        binding.nameEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onNameChanged(text?.toString() ?: "")
            hasUnsavedData = true
        }
        binding.descriptionEditText.doOnTextChanged { _, _, _, _ ->
            hasUnsavedData = true
        }
    }

    private fun setupKeyboardActions() {
        val hideKeyboard: (Int) -> Boolean = { actionId ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.nameEditText.windowToken, 0)
                binding.nameEditText.clearFocus()
                binding.descriptionEditText.clearFocus()
                true
            } else false
        }
        binding.nameEditText.setOnEditorActionListener { _, actionId, _ -> hideKeyboard(actionId) }
        binding.descriptionEditText.setOnEditorActionListener { _, actionId, _ -> hideKeyboard(actionId) }
    }

    protected open fun setupCreateButton() {
        binding.createButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            viewModel.createPlaylist(
                name = name,
                description = binding.descriptionEditText.text.toString()
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

    protected open fun handleBackPress() {
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

    protected open fun observeViewModel() {
        viewModel.observeNameState().observe(viewLifecycleOwner) { state ->
            binding.createButton.isEnabled = !state.isEmpty
        }
    }

    protected fun dpToPx(dp: Float): Int {
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
