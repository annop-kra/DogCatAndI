package com.example.dogcatandi.domain.usecase

import com.example.dogcatandi.domain.model.UserProfile
import com.example.dogcatandi.domain.repository.UserRepository

class GetRandomUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): UserProfile? {
        return userRepository.getRandomUser()
    }
}
