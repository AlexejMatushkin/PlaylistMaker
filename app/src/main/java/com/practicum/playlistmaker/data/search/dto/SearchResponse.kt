package com.practicum.playlistmaker.data.search.dto

import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.data.dto.Response

@Suppress("unused")
class SearchResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<TrackDto>
) : Response()