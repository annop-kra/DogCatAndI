package com.example.dogcatandi.presentation.cats

import androidx.paging.PagingData
import com.example.dogcatandi.domain.model.CatBreed

sealed class CatsUiState {
    data object Loading : CatsUiState()
    data class Success(val breeds: PagingData<CatBreed>) : CatsUiState()
    data class Error(val message: String) : CatsUiState()
}
