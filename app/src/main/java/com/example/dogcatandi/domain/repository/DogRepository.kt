package com.example.dogcatandi.domain.repository

interface DogRepository {
    suspend fun getRandomDogImages(count: Int): List<Result<String>>
}
