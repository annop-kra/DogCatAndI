package com.example.dogcatandi.data.repository

import android.util.Log
import com.example.dogcatandi.data.datasource.remote.DogRemoteDataSource
import com.example.dogcatandi.domain.repository.DogRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class DogRepositoryImpl(
    private val dataSource: DogRemoteDataSource
) : DogRepository {
    override suspend fun getRandomDogImages(count: Int): List<Result<String>> {
        return coroutineScope {
            val deferredList = List(count) { async { dataSource.getRandomDogImage() } }
            deferredList.awaitAll()
        }
    }
}
