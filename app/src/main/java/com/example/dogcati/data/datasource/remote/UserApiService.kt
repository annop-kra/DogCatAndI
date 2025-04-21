package com.example.dogcati.data.datasource.remote

import com.example.dogcati.data.datasource.remote.response.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApiService {
    @GET("api/")
    suspend fun getRandomUser(): UserResponse
}
