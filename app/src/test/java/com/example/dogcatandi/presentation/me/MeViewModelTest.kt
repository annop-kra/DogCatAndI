package com.example.dogcatandi.presentation.me

import app.cash.turbine.test
import com.example.dogcatandi.domain.model.UserProfile
import com.example.dogcatandi.domain.usecase.GetRandomUserUseCase
import io.mockk.coEvery
import io.mockk.mockk
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

@ExperimentalCoroutinesApi
class MeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var getRandomUserUseCase: GetRandomUserUseCase
    private lateinit var viewModel: MeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getRandomUserUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserProfile updates to Success when user loads successfully`() = testScope.runTest {
        val mockUser = UserProfile(
            title = "Mr",
            firstName = "John",
            lastName = "Doe",
            gender = "male",
            dateOfBirth = "01/01/1990",
            nationality = "US",
            phone = "123-456-7890",
            address = "123 Main St",
            age = 31,
            profileImageUrl = "https://example.com/profile.jpg"
        )

        coEvery { getRandomUserUseCase() } returns mockUser

        viewModel = MeViewModel(getRandomUserUseCase)
        advanceUntilIdle()

        val states = mutableListOf<MeUiState>()
        val job = launch { viewModel.uiState.toList(states) }
        advanceUntilIdle()

        assertTrue(states.last() is MeUiState.Success)

        if (states.last() is MeUiState.Success) {
            val success = states.last() as MeUiState.Success
            assertEquals("John", success.userProfile.firstName)
        }

        job.cancel()
    }

    @Test
    fun `loadUserProfile updates to Error when API returns null`() = testScope.runTest {
        coEvery { getRandomUserUseCase() } returns null

        viewModel = MeViewModel(getRandomUserUseCase)
        advanceUntilIdle()

        val states = mutableListOf<MeUiState>()
        val job = launch { viewModel.uiState.toList(states) }
        advanceUntilIdle()

        assertTrue(states.last() is MeUiState.Error)
        assertEquals("Failed to load user profile", (states.last() as MeUiState.Error).message)

        job.cancel()
    }

    @Test
    fun `loadUserProfile handles exceptions`() = testScope.runTest {
        coEvery { getRandomUserUseCase() } throws Exception("Network error")

        viewModel = MeViewModel(getRandomUserUseCase)
        advanceUntilIdle()

        val states = mutableListOf<MeUiState>()
        val job = launch { viewModel.uiState.toList(states) }
        advanceUntilIdle()

        assertTrue(states.last() is MeUiState.Error)
        assertEquals(
            "Failed to load user profile: Network error",
            (states.last() as MeUiState.Error).message
        )

        job.cancel()
    }

    @Test
    fun `loadUserProfile can be manually called`() = testScope.runTest {
        val mockUser = UserProfile(
            title = "Mr",
            firstName = "John",
            lastName = "Doe",
            gender = "male",
            dateOfBirth = "01/01/1990",
            nationality = "US",
            phone = "123-456-7890",
            address = "123 Main St",
            age = 31,
            profileImageUrl = "https://example.com/profile.jpg"
        )
        coEvery { getRandomUserUseCase() } returns mockUser

        viewModel = MeViewModel(getRandomUserUseCase)
        viewModel.uiState.test {
            awaitItem() // Loading จาก init
            awaitItem() // Success จาก init

            viewModel.loadUserProfile()
            awaitItem() // Loading รอบใหม่
            val final = awaitItem()
            assert(final is MeUiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

}
