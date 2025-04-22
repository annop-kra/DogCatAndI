package com.example.dogcatandi.data.repository

import com.example.dogcatandi.data.datasource.remote.UserRemoteDataSource
import com.example.dogcatandi.domain.model.UserProfile
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {

    private val dataSource: UserRemoteDataSource = mockk()
    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setup() {
        repository = UserRepositoryImpl(dataSource)
    }

    @Test
    fun `getRandomUser returns user when data source succeeds`() = runTest {
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

        coEvery { dataSource.getRandomUser() } returns Result.success(user)

        val result = repository.getRandomUser()

        assertEquals(user, result)
    }

    @Test
    fun `getRandomUser returns null when data source fails`() = runTest {
        coEvery { dataSource.getRandomUser() } returns Result.failure(Exception("Network error"))

        val result = repository.getRandomUser()

        assertNull(result)
    }
}
