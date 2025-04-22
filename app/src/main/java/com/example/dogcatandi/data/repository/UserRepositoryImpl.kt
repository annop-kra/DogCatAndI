package com.example.dogcatandi.data.repository

import android.util.Log
import com.example.dogcatandi.data.datasource.remote.UserRemoteDataSource
import com.example.dogcatandi.domain.model.UserProfile
import com.example.dogcatandi.domain.repository.UserRepository

class UserRepositoryImpl(
    private val dataSource: UserRemoteDataSource
) : UserRepository {
    override suspend fun getRandomUser(): UserProfile? {
        Log.d("UserRepositoryImpl", "Fetching random user")
        return dataSource.getRandomUser().fold(
            onSuccess = { user ->
                Log.d("UserRepositoryImpl", "User fetched: $user")
                user
            },
            onFailure = { error ->
                Log.e("UserRepositoryImpl", "Error: ${error.message}", error)
                null
            }
        )
    }
}
