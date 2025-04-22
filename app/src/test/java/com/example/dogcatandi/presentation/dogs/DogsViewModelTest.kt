package com.example.dogcatandi.presentation.dogs

import com.example.dogcatandi.domain.usecase.GetDogImagesUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
            assertEquals("https://example.com/dog1.jpg", state.images[0]?.url)
            assertNotNull(state.images[0])
            assertNotNull(state.images[1])
            assertNotNull(state.images[2])
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
            val nonNullImages = state.images.filterNotNull()
            assertEquals(2, nonNullImages.size)

            val urls = nonNullImages.map { it.url }
            assertTrue(urls.contains("https://example.com/dog1.jpg"))
            assertTrue(urls.contains("https://example.com/dog3.jpg"))
        }
    }

    @Test
    fun `loadDogImagesSequentially loads images one by one`() = testScope.runTest {

        coEvery { getDogImagesUseCase(1) } returnsMany listOf(
            listOf(Result.success("https://example.com/dog1.jpg")),
            listOf(Result.success("https://example.com/dog2.jpg")),
            listOf(Result.success("https://example.com/dog3.jpg"))
        )

        viewModel = DogsViewModel(getDogImagesUseCase)

        clearAllMocks(answers = false)

        val statesCollected = mutableListOf<DogsUiState>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect {
                statesCollected.add(it)
            }
        }

        viewModel.loadDogImagesSequentially()

        advanceUntilIdle()

        val successStates = statesCollected.filterIsInstance<DogsUiState.Success>()

        assertTrue("Should have at least 4 success states, found ${successStates.size}",
            successStates.size >= 4)

        val initialState = successStates.first()
        assertEquals(3, initialState.images.size)
        assertTrue(initialState.images.all { it == null })

        val finalState = successStates.last()
        assertEquals(3, finalState.images.size)
        assertNotNull(finalState.images[0])
        assertNotNull(finalState.images[1])
        assertNotNull(finalState.images[2])

        assertEquals("https://example.com/dog1.jpg", finalState.images[0]?.url)
        assertEquals("https://example.com/dog2.jpg", finalState.images[1]?.url)
        assertEquals("https://example.com/dog3.jpg", finalState.images[2]?.url)

        val firstImageOnlyState = successStates.firstOrNull { state ->
            state.images[0] != null && state.images[1] == null && state.images[2] == null
        }
        assertNotNull("Should have a state with only first image loaded", firstImageOnlyState)

        val firstTwoImagesState = successStates.firstOrNull { state ->
            state.images[0] != null && state.images[1] != null && state.images[2] == null
        }
        assertNotNull("Should have a state with only first two images loaded", firstTwoImagesState)

        job.cancel()
    }

    @Test
    fun `loadDogImagesSequentially uses correct delay based on second digit`() = testScope.runTest {
        every { LocalDateTime.now().second } returns 5

        val apiCallTimes = mutableListOf<Long>()

        coEvery { getDogImagesUseCase(1) } answers {
            apiCallTimes.add(testScope.currentTime)
            listOf(Result.success("https://example.com/dog${apiCallTimes.size}.jpg"))
        }

        viewModel = DogsViewModel(getDogImagesUseCase)
        viewModel.loadDogImagesSequentially()

        advanceUntilIdle()

        assertEquals(3, apiCallTimes.size)

        if (apiCallTimes.size >= 3) {
            val delay1to2 = apiCallTimes[1] - apiCallTimes[0]
            val delay2to3 = apiCallTimes[2] - apiCallTimes[1]

            assertEquals(3000, delay1to2)
            assertEquals(3000, delay2to3)
        }

        apiCallTimes.clear()
        every { LocalDateTime.now().second } returns 4

        viewModel.loadDogImagesSequentially()
        advanceUntilIdle()

        if (apiCallTimes.size >= 3) {
            val delay1to2 = apiCallTimes[1] - apiCallTimes[0]
            val delay2to3 = apiCallTimes[2] - apiCallTimes[1]

            assertEquals(2000, delay1to2)
            assertEquals(2000, delay2to3)
        }
    }
}
