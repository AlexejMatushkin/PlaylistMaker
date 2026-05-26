package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.search.dto.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") term: String): SearchResponse
}
