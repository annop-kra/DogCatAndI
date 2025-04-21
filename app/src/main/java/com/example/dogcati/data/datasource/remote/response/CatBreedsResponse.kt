package com.example.dogcati.data.datasource.remote.response

import com.squareup.moshi.Json

data class CatBreedsResponse(
    @Json(name = "current_page") val currentPage: Int?,
    @Json(name = "data") val data: List<CatBreed>?,
    @Json(name = "last_page") val lastPage: Int?,
    @Json(name = "next_page_url") val nextPageUrl: String?,
    @Json(name = "prev_page_url") val prevPageUrl: String?
)

data class CatBreed(
    @Json(name = "breed") val breed: String?,
    @Json(name = "country") val country: String?,
    @Json(name = "origin") val origin: String?,
    @Json(name = "coat") val coat: String?,
    @Json(name = "pattern") val pattern: String?
)
