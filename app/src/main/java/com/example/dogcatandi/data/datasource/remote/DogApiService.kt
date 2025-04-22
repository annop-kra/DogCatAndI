package com.example.dogcatandi.data.datasource.remote

import com.example.dogcatandi.data.datasource.remote.response.DogImageResponse
import retrofit2.http.GET

interface DogApiService {
    @GET("api/breeds/image/random")
    suspend fun getRandomDogImage(): DogImageResponse
}
