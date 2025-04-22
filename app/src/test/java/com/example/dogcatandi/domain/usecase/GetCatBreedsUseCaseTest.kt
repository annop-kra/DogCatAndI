package com.example.dogcatandi.domain.usecase

import androidx.paging.PagingData
import app.cash.turbine.test
import com.example.dogcatandi.domain.model.CatBreed
import com.example.dogcatandi.domain.repository.CatRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetCatBreedsUseCaseTest {

    private val repository: CatRepository = mockk()
    private lateinit var useCase: GetCatBreedsUseCase

    @Before
    fun setup() {
        useCase = GetCatBreedsUseCase(repository)
    }

    @Test
    fun `invoke returns PagingData from repository`() = runTest {
        val pagingData = PagingData.from(listOf(
            CatBreed("Abyssinian", "Ethiopia", "Natural/Standard", "Short", "Ticked")
        ))
        every { repository.getAllCatBreeds() } returns flowOf(pagingData)

        useCase().test {
            val result = awaitItem()
            assertTrue(result is PagingData)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
