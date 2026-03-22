package com.practicum.playlistmaker.data.dto

import com.google.gson.annotations.SerializedName

class SearchResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<TrackDto>
) : Response()
