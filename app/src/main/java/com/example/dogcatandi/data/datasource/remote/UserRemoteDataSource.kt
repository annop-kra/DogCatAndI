package com.example.dogcatandi.data.datasource.remote

import com.example.dogcatandi.data.mapper.toDomainUserProfile
import com.example.dogcatandi.domain.model.UserProfile
import retrofit2.HttpException
import java.io.IOException

class UserRemoteDataSource(
    private val apiService: UserApiService
) {
    suspend fun getRandomUser(): Result<UserProfile> {
        return try {
            val response = apiService.getRandomUser()
            val user = response.results?.firstOrNull()?.toDomainUserProfile()
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("No user found"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: HttpException) {
            Result.failure(Exception("Server error: ${e.message}"))
        }
    }
}
