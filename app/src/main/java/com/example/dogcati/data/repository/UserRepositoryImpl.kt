package com.example.dogcati.data.repository

import com.example.dogcati.data.datasource.remote.UserApiService
import com.example.dogcati.data.mapper.toUserProfile
import com.example.dogcati.domain.model.UserProfile
import com.example.dogcati.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userApiService: UserApiService
) : UserRepository {
    override suspend fun getRandomUser(): UserProfile? {
        return userApiService.getRandomUser().toUserProfile()
    }
}
