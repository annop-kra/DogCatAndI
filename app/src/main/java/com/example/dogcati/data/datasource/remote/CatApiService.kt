package com.example.dogcati.data.datasource.remote

import com.example.dogcati.data.datasource.remote.response.CatBreedsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CatApiService {
    @GET("breeds")
    suspend fun getCatBreeds(@Query("page") page: Int): CatBreedsResponse
}
