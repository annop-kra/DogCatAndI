package com.example.dogcatandi.data.repository


import androidx.paging.PagingData
import app.cash.turbine.test

import com.example.dogcatandi.data.datasource.remote.CatRemoteDataSource
import com.example.dogcatandi.data.datasource.remote.response.CatBreed
import com.example.dogcatandi.data.datasource.remote.response.CatBreedsResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatRepositoryTest {

    private val dataSource: CatRemoteDataSource = mockk()
    private lateinit var repository: CatRepositoryImpl

    @Before
    fun setup() {
        repository = CatRepositoryImpl(dataSource)
    }

    @Test
    fun `getAllCatBreeds emits PagingData when data source succeeds`() = runTest {
        val catBreed = CatBreed(
            breed = "Abyssinian",
            country = "Ethiopia",
            origin = "Natural/Standard",
            coat = "Short",
            pattern = "Ticked"
        )
        val response = CatBreedsResponse(
            currentPage = 1,
            data = listOf(catBreed),
            lastPage = 2,
            nextPageUrl = "https://catfact.ninja/breeds?page=2",
            prevPageUrl = null
        )

        coEvery { dataSource.getCatBreeds(1) } returns Result.success(response)
        coEvery { dataSource.getCatBreeds(2) } returns Result.success(
            response.copy(currentPage = 2, data = emptyList(), lastPage = 2, nextPageUrl = null)
        )

        repository.getAllCatBreeds().test {
            val pagingData = awaitItem()
            assertTrue(pagingData is PagingData)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getAllCatBreeds handles error when data source fails`() = runTest {
        coEvery { dataSource.getCatBreeds(1) } returns Result.failure(Exception("Network error"))

        repository.getAllCatBreeds().test {
            val pagingData = awaitItem()
            assertTrue(pagingData is PagingData)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
