package com.practicum.playlistmaker

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApiService {

    @GET("search")
    fun search(
        @Query("term") text: String,
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 50
    ): Call<SearchResponse>

    companion object {
        private const val BASE_URL = "https://itunes.apple.com/"

        fun create(): iTunesApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(iTunesApiService::class.java)
        }
    }
}
