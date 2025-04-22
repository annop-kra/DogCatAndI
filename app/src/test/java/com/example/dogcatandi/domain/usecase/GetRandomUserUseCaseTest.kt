package com.example.dogcatandi.domain.usecase

import com.example.dogcatandi.domain.model.UserProfile
import com.example.dogcatandi.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetRandomUserUseCaseTest {

    private val repository: UserRepository = mockk()
    private lateinit var useCase: GetRandomUserUseCase

    @Before
    fun setup() {
        useCase = GetRandomUserUseCase(repository)
    }

    @Test
    fun `invoke returns user when repository succeeds`() = runTest {
        val user = UserProfile(
            title = "Mrs",
            firstName = "Synnøve",
            lastName = "Dyrhaug",
            dateOfBirth = "1970-01-13T12:10:37.249Z",
            age = 55,
            gender = "female",
            nationality = "NO",
            phone = "85471569",
            address = "2406 Nyjordeveien, Åneby, Description, Norway, 7049",
            profileImageUrl = "https://randomuser.me/api/portraits/women/50.jpg"
        )
        coEvery { repository.getRandomUser() } returns user

        val result = useCase()

        assertEquals(user, result)
    }

    @Test
    fun `invoke returns null when repository fails`() = runTest {
        coEvery { repository.getRandomUser() } returns null

        val result = useCase()

        assertNull(result)
    }
}
