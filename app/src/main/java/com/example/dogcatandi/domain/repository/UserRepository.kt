package com.example.dogcatandi.domain.repository

import com.example.dogcatandi.domain.model.UserProfile

interface UserRepository {
    suspend fun getRandomUser(): UserProfile?
}