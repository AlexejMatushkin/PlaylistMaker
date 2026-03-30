package com.practicum.playlistmaker.domain.sharing.impl

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.sharing.ExternalNavigator
import com.practicum.playlistmaker.domain.sharing.interactor.SharingInteractor
import com.practicum.playlistmaker.domain.sharing.models.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val context: Context
) : SharingInteractor {

    override fun shareApp() {
        val shareText = context.getString(R.string.share_app_text)
        externalNavigator.shareLink(shareText)
    }

    override fun openTerms() {
        val termsUrl = context.getString(R.string.user_agreement_url)
        externalNavigator.openLink(termsUrl)
    }

    override fun openSupport() {
        val emailData = EmailData(
            email = context.getString(R.string.support_email),
            subject = context.getString(R.string.support_subject),
            body = context.getString(R.string.support_body)
        )
        externalNavigator.openEmail(emailData)
    }
}