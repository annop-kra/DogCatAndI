package com.example.dogcatandi.domain.repository

import androidx.paging.PagingData
import com.example.dogcatandi.domain.model.CatBreed
import kotlinx.coroutines.flow.Flow

interface CatRepository {
    fun getAllCatBreeds(): Flow<PagingData<CatBreed>>
}
