package com.example.dogcati.domain.repository

import com.example.dogcati.domain.model.UserProfile

interface UserRepository {
    suspend fun getRandomUser(): UserProfile?
}