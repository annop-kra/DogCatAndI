package com.example.dogcatandi.domain.usecase

import androidx.paging.PagingData
import com.example.dogcatandi.domain.model.CatBreed
import com.example.dogcatandi.domain.repository.CatRepository
import kotlinx.coroutines.flow.Flow

class GetCatBreedsUseCase(
    private val catRepository: CatRepository
) {
    operator fun invoke(): Flow<PagingData<CatBreed>> {
        return catRepository.getAllCatBreeds()
    }
}
