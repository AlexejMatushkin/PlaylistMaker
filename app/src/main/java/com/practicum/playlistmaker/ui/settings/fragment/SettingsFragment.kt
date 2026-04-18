package com.practicum.playlistmaker.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModel()
    private var isUpdatingFromViewModel = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupShareButton()
        setupSupportButton()
        setupAgreementButton()
        setupThemeSwitcher()
        observeViewModel()
    }

    private fun setupShareButton() {
        binding.shareButton.setOnClickListener {
            val shareText = getString(R.string.share_app_text)
            val shareTitle = getString(R.string.share_app_name)
            viewModel.shareApp(shareText, shareTitle)
        }
    }

    private fun setupSupportButton() {
        binding.supportButton.setOnClickListener {
            val email = getString(R.string.support_email)
            val subject = getString(R.string.support_subject)
            val body = getString(R.string.support_body)
            viewModel.sendSupport(email, subject, body)
        }
    }

    private fun setupAgreementButton() {
        binding.agreementButton.setOnClickListener {
            val termsUrl = getString(R.string.user_agreement_url)
            viewModel.openAgreement(termsUrl)
        }
    }

    private fun setupThemeSwitcher() {
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isUpdatingFromViewModel) {
                viewModel.switchTheme(isChecked)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.themeState.observe(viewLifecycleOwner) { isDarkTheme ->
            if (binding.themeSwitch.isChecked != isDarkTheme) {
                isUpdatingFromViewModel = true
                binding.themeSwitch.isChecked = isDarkTheme
                isUpdatingFromViewModel = false
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
