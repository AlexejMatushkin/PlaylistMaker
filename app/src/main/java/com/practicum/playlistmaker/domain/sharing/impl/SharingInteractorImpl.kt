package com.practicum.playlistmaker.domain.sharing.impl

import com.practicum.playlistmaker.domain.sharing.ExternalNavigator
import com.practicum.playlistmaker.domain.sharing.interactor.SharingInteractor
import com.practicum.playlistmaker.domain.sharing.models.EmailData


class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    override fun shareApp(shareText: String, shareTitle: String) {
        externalNavigator.shareLink(shareText, shareTitle)
    }

    override fun openTerms(termsUrl: String) {
        externalNavigator.openLink(termsUrl)
    }

    override fun openSupport(email: String, subject: String, body: String) {
        externalNavigator.openEmail(EmailData(email, subject, body))
    }
}
