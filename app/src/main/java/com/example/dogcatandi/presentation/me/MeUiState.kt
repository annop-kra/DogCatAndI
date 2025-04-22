package com.example.dogcatandi.presentation.me

import com.example.dogcatandi.domain.model.UserProfile

sealed class MeUiState {
    data object Loading : MeUiState()
    data class Success(val userProfile: UserProfile) : MeUiState()
    data class Error(val message: String) : MeUiState()
}
