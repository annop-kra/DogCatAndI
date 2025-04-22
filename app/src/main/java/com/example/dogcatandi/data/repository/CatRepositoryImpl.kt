package com.example.dogcatandi.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.dogcatandi.data.datasource.remote.CatRemoteDataSource
import com.example.dogcatandi.data.datasource.remote.source.CatPagingSource
import com.example.dogcatandi.domain.model.CatBreed
import com.example.dogcatandi.domain.repository.CatRepository
import kotlinx.coroutines.flow.Flow

class CatRepositoryImpl(
    private val dataSource: CatRemoteDataSource
) : CatRepository {
    override fun getAllCatBreeds(): Flow<PagingData<CatBreed>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CatPagingSource(dataSource)
            }
        ).flow
    }
}
