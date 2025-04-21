package com.example.dogcati.data.datasource.remote.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.dogcati.data.datasource.remote.CatApiService
import com.example.dogcati.data.mapper.toDomainCatBreed
import java.io.IOException
import com.example.dogcati.domain.model.CatBreed as DomainCatBreed

class CatPagingSource(
    private val apiService: CatApiService
) : PagingSource<Int, DomainCatBreed>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DomainCatBreed> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getCatBreeds(page)
            val breeds = response.data?.mapNotNull { it.toDomainCatBreed() } ?: emptyList()
            val nextKey = if (response.nextPageUrl != null && page < (response.lastPage ?: Int.MAX_VALUE)) page + 1 else null
            val prevKey = if (page > 1) page - 1 else null

            LoadResult.Page(
                data = breeds,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            LoadResult.Error(Exception("Failed to load cat breeds: ${e.message}"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DomainCatBreed>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
