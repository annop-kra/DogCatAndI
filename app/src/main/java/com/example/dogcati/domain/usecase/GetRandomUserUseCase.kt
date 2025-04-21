package com.example.dogcati.domain.usecase

import com.example.dogcati.domain.model.UserProfile
import com.example.dogcati.domain.repository.UserRepository

class GetRandomUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): UserProfile? {
        return userRepository.getRandomUser()
    }
}
