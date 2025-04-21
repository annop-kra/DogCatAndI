package com.example.dogcati.domain.repository

import androidx.paging.PagingData
import com.example.dogcati.domain.model.CatBreed
import kotlinx.coroutines.flow.Flow

interface CatRepository {
    fun getAllCatBreeds(): Flow<PagingData<CatBreed>>
}
