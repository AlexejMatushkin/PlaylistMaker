package com.practicum.playlistmaker.domain.sharing.interactor

interface SharingInteractor {
    fun shareApp(shareText: String, shareTitle: String)
    fun openTerms(termsUrl: String)
    fun openSupport(email: String, subject: String, body: String)
}
