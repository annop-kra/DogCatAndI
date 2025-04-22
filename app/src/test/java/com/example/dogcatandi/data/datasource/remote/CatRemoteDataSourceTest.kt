package com.example.dogcatandi.data.datasource.remote

import com.example.dogcatandi.data.datasource.remote.response.CatBreed
import com.example.dogcatandi.data.datasource.remote.response.CatBreedsResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CatRemoteDataSourceTest {

    private val apiService: CatApiService = mockk()
    private lateinit var dataSource: CatRemoteDataSource

    @Before
    fun setup() {
        dataSource = CatRemoteDataSource(apiService)
    }

    @Test
    fun `getCatBreeds returns success when API call is successful`() = runTest {
        val response = CatBreedsResponse(
            currentPage = 1,
            data = listOf(
                CatBreed(
                    breed = "American Curl",
                    country = "United States",
                    origin = "Mutation",
                    coat = "Short/Long",
                    pattern = "All"
                )
            ),
            lastPage = 2,
            nextPageUrl = "https://catfact.ninja/breeds?page=2",
            prevPageUrl = null
        )

        coEvery { apiService.getCatBreeds(1) } returns response

        val result = dataSource.getCatBreeds(1)

        assertTrue(result.isSuccess)
        assertEquals(response, result.getOrNull())
    }

    @Test
    fun `getCatBreeds returns failure when API call throws IOException`() = runTest {
        coEvery { apiService.getCatBreeds(1) } throws IOException("Network error")

        val result = dataSource.getCatBreeds(1)

        assertTrue(result.isFailure)
        assertEquals("Network error: Network error", result.exceptionOrNull()?.message)
    }
}
