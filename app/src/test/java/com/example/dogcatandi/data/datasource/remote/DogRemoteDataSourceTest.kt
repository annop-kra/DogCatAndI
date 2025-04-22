package com.example.dogcatandi.data.datasource.remote

import com.example.dogcatandi.data.datasource.remote.response.DogImageResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class DogRemoteDataSourceTest {

    private val apiService: DogApiService = mockk()
    private lateinit var dataSource: DogRemoteDataSource

    @Before
    fun setup() {
        dataSource = DogRemoteDataSource(apiService)
    }

    @Test
    fun `getRandomDogImage returns success when API call is successful`() = runTest {
        val response = DogImageResponse(
            message = "https://images.dog.ceo/breeds/entlebucher/n02108000_1948.jpg",
            status = "success"
        )

        coEvery { apiService.getRandomDogImage() } returns response

        val result = dataSource.getRandomDogImage()

        assertTrue(result.isSuccess)
        assertEquals(response.message, result.getOrNull())
    }

    @Test
    fun `getRandomDogImage returns failure when API call throws IOException`() = runTest {
        coEvery { apiService.getRandomDogImage() } throws IOException("Network error")

        val result = dataSource.getRandomDogImage()

        assertTrue(result.isFailure)
        assertEquals("Network error: Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getRandomDogImage returns failure when API status is not success`() = runTest {
        val response = DogImageResponse(
            message = "",
            status = "error"
        )

        coEvery { apiService.getRandomDogImage() } returns response

        val result = dataSource.getRandomDogImage()

        assertTrue(result.isFailure)
        assertEquals("Invalid response from API", result.exceptionOrNull()?.message)
    }
}
