package com.example.dogcatandi.domain.usecase

import com.example.dogcatandi.domain.repository.DogRepository

class GetDogImagesUseCase(
    private val repository: DogRepository
) {
    suspend operator fun invoke(count: Int): List<Result<String>> {
        return repository.getRandomDogImages(count)
    }
}
