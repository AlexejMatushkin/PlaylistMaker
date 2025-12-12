package com.practicum.playlistmaker

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchRepository(private val apiService: iTunesApiService) {

    fun searchTracks(
        query: String,
        callback: SearchCallback
    ) {
        val call = apiService.search(query)
        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    if (searchResponse != null) {
                        val tracks = searchResponse.results.mapNotNull { it.toTrack() }
                        callback.onSuccess(tracks)
                    } else {
                        callback.onError("Server error: empty response")
                    }
                } else {
                    callback.onError("Server error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                callback.onError("Network error: ${t.message}")
            }
        })
    }

    interface SearchCallback {
        fun onSuccess(tracks: List<Track>)
        fun onError(error: String)
    }
}
