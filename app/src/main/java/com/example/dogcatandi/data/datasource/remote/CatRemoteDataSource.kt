package com.example.dogcatandi.data.datasource.remote

import com.example.dogcatandi.data.datasource.remote.response.CatBreedsResponse
import retrofit2.HttpException
import java.io.IOException

class CatRemoteDataSource(
    private val apiService: CatApiService
) {
    suspend fun getCatBreeds(page: Int): Result<CatBreedsResponse> {
        return try {
            val response = apiService.getCatBreeds(page)
            Result.success(response)
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: HttpException) {
            Result.failure(Exception("Server error: ${e.message}"))
        }
    }
}
