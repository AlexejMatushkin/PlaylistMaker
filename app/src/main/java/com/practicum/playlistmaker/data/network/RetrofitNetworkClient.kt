package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.search.dto.SearchRequest
import java.io.IOException

class RetrofitNetworkClient(
    private val iTunesApiService: ITunesApiService
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        return if (dto is SearchRequest) {
            try {
                val response = iTunesApiService.search(dto.expression)
                response.apply { resultCode = 200 }
            } catch (_: IOException) {
                Response().apply { resultCode = -1 }
            } catch (_: Exception) {
                Response().apply { resultCode = -2 }
            }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}
