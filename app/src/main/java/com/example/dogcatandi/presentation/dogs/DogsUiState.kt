package com.example.dogcatandi.presentation.dogs

import org.threeten.bp.LocalDateTime

data class DogImage(
    val url: String,
    val timestamp: LocalDateTime
)

sealed class DogsUiState {
    data object Loading : DogsUiState()
    data class Success(val images: List<DogImage>) : DogsUiState()
    data class Error(val message: String) : DogsUiState()
}
