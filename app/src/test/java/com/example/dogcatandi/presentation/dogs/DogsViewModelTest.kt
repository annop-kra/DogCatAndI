package com.example.dogcatandi.presentation.dogs

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.dogcatandi.domain.usecase.GetDogImagesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class DogsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val getDogImagesUseCase: GetDogImagesUseCase = mockk()
    private lateinit var viewModel: DogsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DogsViewModel(getDogImagesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDogImagesConcurrently emits Loading then Success when use case returns images`() = runTest(testDispatcher) {
        val imageUrls = listOf(
            Result.success("https://images.dog.ceo/breeds/entlebucher/n02108000_1948.jpg"),
            Result.success("https://images.dog.ceo/breeds/hound/n02089867_1234.jpg"),
            Result.success("https://images.dog.ceo/breeds/retriever/n02099849_5678.jpg")
        )

        coEvery { getDogImagesUseCase(3) } returns imageUrls

        val states = mutableListOf<DogsUiState>()
        viewModel.uiState.take(2).toList(states)
        Log.d("DogsViewModelTest", "Initial states: $states")

        viewModel.loadDogImagesConcurrently()
        testDispatcher.scheduler.advanceUntilIdle()

        Log.d("DogsViewModelTest", "States after load: $states")
        assertEquals(2, states.size)
        assertTrue(states[0] is DogsUiState.Loading)
        assertTrue(states[1] is DogsUiState.Success)
        assertEquals(3, (states[1] as DogsUiState.Success).images.size)
    }

    @Test
    fun `loadDogImagesSequentially emits Error when all use case calls fail`() = runTest(testDispatcher) {
        // ตั้งค่าตัวทดสอบให้ทุกคำขอจะล้มเหลว
        coEvery { getDogImagesUseCase(1) } returns listOf(Result.failure(Exception("Network error")))

        // เรียกฟังก์ชันที่ต้องการทดสอบ
        viewModel.loadDogImagesSequentially()

        // รอให้ StateFlow เสร็จสิ้นการอัปเดตค่า
        advanceUntilIdle()  // รอจนกระทั่งสถานะที่ทำงานเสร็จ

        // ทดสอบสถานะ Loading
        val loadingState = viewModel.uiState.first()
        assertTrue(loadingState is DogsUiState.Loading)

        // ทดสอบสถานะ Error หลังจากที่โหลดเสร็จ
        val finalState = viewModel.uiState.first { it !is DogsUiState.Loading }
        assertTrue(finalState is DogsUiState.Error)
        assertEquals("Failed to load dog images", (finalState as DogsUiState.Error).message)
    }


}