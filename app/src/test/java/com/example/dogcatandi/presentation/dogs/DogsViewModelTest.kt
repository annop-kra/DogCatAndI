package com.example.dogcatandi.presentation.dogs

import com.example.dogcatandi.domain.usecase.GetDogImagesUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.threeten.bp.LocalDateTime

@ExperimentalCoroutinesApi
class DogsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var getDogImagesUseCase: GetDogImagesUseCase
    private lateinit var viewModel: DogsViewModel
    private val mockTime = LocalDateTime.of(2025, 4, 22, 14, 30, 0)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getDogImagesUseCase = mockk()
        mockkStatic(LocalDateTime::class)
        every { LocalDateTime.now() } returns mockTime
        every { LocalDateTime.now().second } returns 2
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(LocalDateTime::class)
    }

    @Test
    fun `loadDogImagesConcurrently updates to Success when all images load successfully`() = testScope.runTest {
        val dogUrls = listOf(
            Result.success("https://example.com/dog1.jpg"),
            Result.success("https://example.com/dog2.jpg"),
            Result.success("https://example.com/dog3.jpg")
        )

        coEvery { getDogImagesUseCase(3) } returns dogUrls

        viewModel = DogsViewModel(getDogImagesUseCase)
        advanceUntilIdle()

        // เช็ค state สุดท้าย
        val state = viewModel.uiState.value
        assertTrue(state is DogsUiState.Success)

        if (state is DogsUiState.Success) {
            assertEquals(3, state.images.size)
            assertEquals("https://example.com/dog1.jpg", state.images[0].url)
        }
    }

    @Test
    fun `loadDogImagesConcurrently updates to Error when all images fail`() = testScope.runTest {
        val dogUrls = listOf<Result<String>>(
            Result.failure(Exception("Failed to load image")),
            Result.failure(Exception("Failed to load image")),
            Result.failure(Exception("Failed to load image"))
        )

        coEvery { getDogImagesUseCase(3) } returns dogUrls

        viewModel = DogsViewModel(getDogImagesUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is DogsUiState.Error)
        assertEquals("Failed to load dog images", (state as DogsUiState.Error).message)
    }

    @Test
    fun `loadDogImagesConcurrently updates to Success with partial results`() = testScope.runTest {
        val dogUrls = listOf(
            Result.success("https://example.com/dog1.jpg"),
            Result.failure(Exception("Failed to load image")),
            Result.success("https://example.com/dog3.jpg")
        )

        coEvery { getDogImagesUseCase(3) } returns dogUrls

        viewModel = DogsViewModel(getDogImagesUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is DogsUiState.Success)

        if (state is DogsUiState.Success) {
            assertEquals(2, state.images.size)
            assertEquals("https://example.com/dog1.jpg", state.images[0].url)
            assertEquals("https://example.com/dog3.jpg", state.images[1].url)
        }
    }

    @Test
    fun `loadDogImagesSequentially loads images successfully`() = testScope.runTest {
        coEvery { getDogImagesUseCase(1) } returnsMany listOf(
            listOf(Result.success("https://example.com/dog1.jpg")),
            listOf(Result.success("https://example.com/dog2.jpg")),
            listOf(Result.success("https://example.com/dog3.jpg"))
        )

        viewModel = DogsViewModel(getDogImagesUseCase)
        advanceUntilIdle()

        viewModel.loadDogImagesSequentially()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is DogsUiState.Success)

        if (state is DogsUiState.Success) {
            assertEquals(3, state.images.size)
        }
    }

    @Test
    fun `loadDogImagesSequentially handles exceptions`() = testScope.runTest {
        coEvery { getDogImagesUseCase(1) } throws Exception("Network error")

        viewModel = DogsViewModel(getDogImagesUseCase)
        advanceUntilIdle()

        viewModel.loadDogImagesSequentially()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is DogsUiState.Error)
        assertEquals("Failed to load dog images: Network error", (state as DogsUiState.Error).message)
    }
}