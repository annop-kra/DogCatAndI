package com.example.dogcatandi.domain.usecase

import com.example.dogcatandi.domain.repository.DogRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetDogImagesUseCaseTest {

    private val repository: DogRepository = mockk()
    private lateinit var useCase: GetDogImagesUseCase

    @Before
    fun setup() {
        useCase = GetDogImagesUseCase(repository)
    }

    @Test
    fun `invoke returns list of success results when repository succeeds`() = runTest {
        val imageUrl = "https://images.dog.ceo/breeds/entlebucher/n02108000_1948.jpg"
        coEvery { repository.getRandomDogImages(3) } returns listOf(
            Result.success(imageUrl),
            Result.success(imageUrl),
            Result.success(imageUrl)
        )

        val results = useCase(3)

        assertEquals(3, results.size)
        results.forEach { result ->
            assertTrue(result.isSuccess)
            assertEquals(imageUrl, result.getOrNull())
        }
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        coEvery { repository.getRandomDogImages(3) } returns listOf(
            Result.success("https://images.dog.ceo/breeds/entlebucher/n02108000_1948.jpg"),
            Result.failure(Exception("Network error")),
            Result.success("https://images.dog.ceo/breeds/hound/n02089867_1234.jpg")
        )

        val results = useCase(3)

        assertEquals(3, results.size)
        assertTrue(results[1].isFailure)
        assertEquals("Network error", results[1].exceptionOrNull()?.message)
    }
}
