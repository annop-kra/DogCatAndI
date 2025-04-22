package com.example.dogcatandi.data.datasource.remote

import androidx.paging.PagingSource
import com.example.dogcatandi.data.datasource.remote.response.CatBreed
import com.example.dogcatandi.data.datasource.remote.response.CatBreedsResponse
import com.example.dogcatandi.data.datasource.remote.source.CatPagingSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class CatPagingSourceTest {

    private val dataSource: CatRemoteDataSource = mockk()
    private lateinit var pagingSource: CatPagingSource

    @Before
    fun setup() {
        pagingSource = CatPagingSource(dataSource)
    }

    @Test
    fun `load returns page when data source succeeds`() = runTest {
        val response = CatBreedsResponse(
            currentPage = 1,
            data = listOf(
                CatBreed(
                    breed = "American Curl",
                    country = "United States",
                    origin = "Mutation",
                    coat = "Short/Long",
                    pattern = "All"
                )
            ),
            lastPage = 2,
            nextPageUrl = "https://catfact.ninja/breeds?page=2",
            prevPageUrl = null
        )

        coEvery { dataSource.getCatBreeds(1) } returns Result.success(response)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.data.size)
        assertEquals("American Curl", page.data[0].breed)
        assertEquals(2, page.nextKey)
        assertEquals(null, page.prevKey)
    }

    @Test
    fun `load returns error when data source fails`() = runTest {
        coEvery { dataSource.getCatBreeds(1) } returns Result.failure(IOException("Network error"))

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        val error = result as PagingSource.LoadResult.Error
        assertEquals("Network error", error.throwable.message)
    }
}
