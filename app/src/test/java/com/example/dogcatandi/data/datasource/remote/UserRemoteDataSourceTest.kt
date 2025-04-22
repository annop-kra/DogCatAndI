package com.example.dogcatandi.data.datasource.remote

import com.example.dogcatandi.data.datasource.remote.response.Coordinates
import com.example.dogcatandi.data.datasource.remote.response.Dob
import com.example.dogcatandi.data.datasource.remote.response.Id
import com.example.dogcatandi.data.datasource.remote.response.Info
import com.example.dogcatandi.data.datasource.remote.response.Location
import com.example.dogcatandi.data.datasource.remote.response.Login
import com.example.dogcatandi.data.datasource.remote.response.Name
import com.example.dogcatandi.data.datasource.remote.response.Picture
import com.example.dogcatandi.data.datasource.remote.response.Registered
import com.example.dogcatandi.data.datasource.remote.response.Street
import com.example.dogcatandi.data.datasource.remote.response.Timezone
import com.example.dogcatandi.data.datasource.remote.response.User
import com.example.dogcatandi.data.datasource.remote.response.UserResponse
import com.example.dogcatandi.data.mapper.toDomainUserProfile
import com.example.dogcatandi.domain.model.UserProfile
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UserRemoteDataSourceTest {

    private val apiService: UserApiService = mockk()
    private lateinit var dataSource: UserRemoteDataSource

    @Before
    fun setup() {
        dataSource = UserRemoteDataSource(apiService)
    }

    @Test
    fun `getRandomUser returns success when API call is successful`() = runTest {
        val response = UserResponse(
            results = listOf(
                User(
                    gender = "female",
                    name = Name(
                        title = "Mrs",
                        first = "Synnøve",
                        last = "Dyrhaug"
                    ),
                    location = Location(
                        street = Street(
                            number = 2406,
                            name = "Nyjordeveien"
                        ),
                        city = "Åneby",
                        state = "Description",
                        country = "Norway",
                        postcode = "7049",
                        coordinates = Coordinates(
                            latitude = "45.0689",
                            longitude = "47.5678"
                        ),
                        timezone = Timezone(
                            offset = "-10:00",
                            description = "Hawaii"
                        )
                    ),
                    email = "synnove.dyrhaug@example.com",
                    login = Login(
                        uuid = "e28139f3-8659-4328-a00e-717c8e86c18a",
                        username = "tinyladybug159",
                        password = "1227",
                        salt = "AvJohFkb",
                        md5 = "99918bec479bf131d25657002353474b",
                        sha1 = "50e3076458d4e1b3990a8bdd07305798ea42a422",
                        sha256 = "709866c8142c4a8ede6c48574d5c5d16862089b39bc90f2788ba54b786d7675b"
                    ),
                    dob = Dob(
                        date = "1970-01-13T12:10:37.249Z",
                        age = 55
                    ),
                    registered = Registered(
                        date = "2017-10-25T05:05:05.530Z",
                        age = 7
                    ),
                    phone = "85471569",
                    cell = "95393343",
                    id = Id(
                        name = "FN",
                        value = "13017014646"
                    ),
                    picture = Picture(
                        large = "https://randomuser.me/api/portraits/women/50.jpg",
                        medium = "https://randomuser.me/api/portraits/med/women/50.jpg",
                        thumbnail = "https://randomuser.me/api/portraits/thumb/women/50.jpg"
                    ),
                    nat = "NO"
                )
            ),
            info = Info(
                seed = "e8e62b80958c1755",
                results = 1,
                page = 1,
                version = "1.4"
            )
        )


        coEvery { apiService.getRandomUser() } returns response

        val result = dataSource.getRandomUser()

        assertTrue(result.isSuccess)
        assertEquals(response.results?.first()?.toDomainUserProfile(), result.getOrNull())
    }

    @Test
    fun `getRandomUser returns failure when API call throws IOException`() = runTest {
        coEvery { apiService.getRandomUser() } throws IOException("Network error")

        val result = dataSource.getRandomUser()

        assertTrue(result.isFailure)
        assertEquals("Network error: Network error", result.exceptionOrNull()?.message)
    }
}
