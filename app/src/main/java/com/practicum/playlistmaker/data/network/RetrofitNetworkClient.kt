package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.search.dto.SearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApiService::class.java)

    override fun doRequest(dto: Any): Response {
        return if (dto is SearchRequest) {
            try {
                val response = iTunesService.search(dto.expression).execute()
                val body = response.body()
                body?.apply { resultCode = response.code() } ?: Response().apply { resultCode = response.code() }
            } catch (_: IOException) {
                Response().apply { resultCode = -1 }
            } catch (_: Exception) {
                Response().apply { resultCode = -2 }
            }
        } else {
            Response().apply { resultCode = 400 }
        }
    }

    companion object {
        private const val BASE_URL = "https://itunes.apple.com"
    }
}
