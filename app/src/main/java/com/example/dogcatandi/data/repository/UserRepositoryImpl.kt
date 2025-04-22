package com.example.dogcatandi.data.repository

import android.util.Log
import com.example.dogcatandi.data.datasource.remote.UserRemoteDataSource
import com.example.dogcatandi.domain.model.UserProfile
import com.example.dogcatandi.domain.repository.UserRepository

class UserRepositoryImpl(
    private val dataSource: UserRemoteDataSource
) : UserRepository {
    override suspend fun getRandomUser(): UserProfile? {
        return dataSource.getRandomUser().fold(
            onSuccess = { user ->
                user
            },
            onFailure = { error ->
                null
            }
        )
    }
}
