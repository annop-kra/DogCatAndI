package com.example.dogcatandi.data.datasource.remote

import retrofit2.HttpException
import java.io.IOException

class DogRemoteDataSource(
    private val apiService: DogApiService
) {
    suspend fun getRandomDogImage(): Result<String> {
        return try {
            val response = apiService.getRandomDogImage()
            if (response.status == "success" && response.message.isNotEmpty()) {
                Result.success(response.message)
            } else {
                Result.failure(Exception("Invalid response from API"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: HttpException) {
            Result.failure(Exception("Server error: ${e.message}"))
        }
    }
}
