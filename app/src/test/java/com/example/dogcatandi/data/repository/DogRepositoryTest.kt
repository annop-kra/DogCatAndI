package com.example.dogcatandi.data.repository

import com.example.dogcatandi.data.datasource.remote.DogRemoteDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DogRepositoryTest {

    private val dataSource: DogRemoteDataSource = mockk()
    private lateinit var repository: DogRepositoryImpl

    @Before
    fun setup() {
        repository = DogRepositoryImpl(dataSource)
    }

    @Test
    fun `getRandomDogImages returns list of success results when data source succeeds`() = runTest {
        val imageUrl = "https://images.dog.ceo/breeds/entlebucher/n02108000_1948.jpg"
        coEvery { dataSource.getRandomDogImage() } returns Result.success(imageUrl)

        val results = repository.getRandomDogImages(3)

        assertEquals(3, results.size)
        results.forEach { result ->
            assertTrue(result.isSuccess)
            assertEquals(imageUrl, result.getOrNull())
        }
    }

    @Test
    fun `getRandomDogImages returns mixed results when data source partially fails`() = runTest {
        coEvery { dataSource.getRandomDogImage() } returnsMany listOf(
            Result.success("https://images.dog.ceo/breeds/entlebucher/n02108000_1948.jpg"),
            Result.failure(Exception("Network error")),
            Result.success("https://images.dog.ceo/breeds/hound/n02089867_1234.jpg")
        )

        val results = repository.getRandomDogImages(3)

        assertEquals(3, results.size)
        assertTrue(results[0].isSuccess)
        assertTrue(results[1].isFailure)
        assertTrue(results[2].isSuccess)
        assertEquals("Network error", results[1].exceptionOrNull()?.message)
    }
}
