package com.example.dogcatandi.data.datasource.remote

import com.example.dogcatandi.data.datasource.remote.response.UserResponse
import retrofit2.http.GET

interface UserApiService {
    @GET("api/")
    suspend fun getRandomUser(): UserResponse
}
