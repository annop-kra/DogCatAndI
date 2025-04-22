package com.example.dogcatandi.data.datasource.remote.response

import com.squareup.moshi.Json

data class DogImageResponse(
    @Json(name = "message") val message: String,
    @Json(name = "status") val status: String
)
