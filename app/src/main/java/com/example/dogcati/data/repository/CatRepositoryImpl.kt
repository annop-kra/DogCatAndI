package com.example.dogcati.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.dogcati.data.datasource.remote.CatApiService
import com.example.dogcati.data.datasource.remote.source.CatPagingSource
import com.example.dogcati.domain.model.CatBreed
import com.example.dogcati.domain.repository.CatRepository
import kotlinx.coroutines.flow.Flow

class CatRepositoryImpl(
    private val apiService: CatApiService
) : CatRepository {
    override fun getAllCatBreeds(): Flow<PagingData<CatBreed>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CatPagingSource(apiService) }
        ).flow
    }
}
