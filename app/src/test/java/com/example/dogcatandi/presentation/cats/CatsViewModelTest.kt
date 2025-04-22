package com.example.dogcatandi.presentation.cats

import androidx.paging.PagingData
import com.example.dogcatandi.domain.model.CatBreed
import com.example.dogcatandi.domain.usecase.GetCatBreedsUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class CatsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var getCatBreedsUseCase: GetCatBreedsUseCase
    private lateinit var viewModel: CatsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCatBreedsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCatBreeds updates to Success when breeds load successfully`() = testScope.runTest {
        // Mock data
        val pagingData = PagingData.from(listOf(
            CatBreed("Abyssinian", "Ethiopia", "Natural/Standard", "Short", "Ticked"),
            CatBreed("Aegean", "Greece", "Natural/Standard", "Semi-long", "Bi- or tri-colored")
        ))

        every { getCatBreedsUseCase() } returns flowOf(pagingData)

        viewModel = CatsViewModel(getCatBreedsUseCase)
        advanceUntilIdle()

        val states = mutableListOf<CatsUiState>()
        val job = launch { viewModel.uiState.toList(states) }
        advanceUntilIdle()

        assertTrue(states.isNotEmpty() && states.last() is CatsUiState.Success)

        job.cancel()
    }

    @Test
    fun `loadCatBreeds updates to Error when API throws exception`() = testScope.runTest {
        val getCatBreedsUseCaseMock = mockk<GetCatBreedsUseCase>(relaxed = true)

        val exception = Exception("Network error")

        every { getCatBreedsUseCaseMock() } answers { throw exception }

        val viewModel = CatsViewModel(getCatBreedsUseCaseMock)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is CatsUiState.Error)
        assertEquals("Failed to load cat breeds: Network error", (state as CatsUiState.Error).message)
    }

    @Test
    fun `loadCatBreeds catches errors in flow`() = testScope.runTest {
        every { getCatBreedsUseCase() } throws Exception("Network error")

        viewModel = CatsViewModel(getCatBreedsUseCase)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is CatsUiState.Error)
    }

    @Test
    fun `loadCatBreeds can be manually called`() = testScope.runTest {
        every { getCatBreedsUseCase() } returns flowOf(PagingData.empty())

        viewModel = CatsViewModel(getCatBreedsUseCase)
        advanceUntilIdle()

        viewModel.loadCatBreeds()
        advanceUntilIdle()

        verify(exactly = 2) { getCatBreedsUseCase() }
    }
}
