package com.example.dogcati.domain.usecase

import androidx.paging.PagingData
import com.example.dogcati.domain.model.CatBreed
import com.example.dogcati.domain.repository.CatRepository
import kotlinx.coroutines.flow.Flow

class GetCatBreedsUseCase(
    private val catRepository: CatRepository
) {
    operator fun invoke(): Flow<PagingData<CatBreed>> {
        return catRepository.getAllCatBreeds()
    }
}
