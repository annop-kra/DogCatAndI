package com.example.dogcatandi.data.datasource.remote.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.dogcatandi.data.datasource.remote.CatRemoteDataSource
import com.example.dogcatandi.data.mapper.toDomainCatBreed
import com.example.dogcatandi.domain.model.CatBreed as DomainCatBreed

class CatPagingSource(
    private val dataSource: CatRemoteDataSource
) : PagingSource<Int, DomainCatBreed>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DomainCatBreed> {
        val page = params.key ?: 1
        return try {
            val response = dataSource.getCatBreeds(page)
            response.fold(
                onSuccess = { breedsResponse ->
                    val breeds = breedsResponse.data?.mapNotNull { it.toDomainCatBreed() } ?: emptyList()
                    val nextKey = if (breedsResponse.nextPageUrl == null || page >= (breedsResponse.lastPage ?: Int.MAX_VALUE)) {
                        null
                    } else {
                        page + 1
                    }
                    LoadResult.Page(
                        data = breeds,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = nextKey
                    )
                },
                onFailure = { error ->
                    LoadResult.Error(error)
                }
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DomainCatBreed>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
