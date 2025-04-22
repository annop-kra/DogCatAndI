package com.example.dogcatandi.presentation.me

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogcatandi.domain.usecase.GetRandomUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MeViewModel(
    private val getRandomUserUseCase: GetRandomUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MeUiState>(MeUiState.Loading)
    val uiState: StateFlow<MeUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = MeUiState.Loading
            try {
                val user = getRandomUserUseCase()
                if (user != null) {
                    _uiState.value = MeUiState.Success(user)
                } else {
                    _uiState.value = MeUiState.Error("Failed to load user profile")
                }
            } catch (e: Exception) {
                _uiState.value = MeUiState.Error("Failed to load user profile: ${e.message}")
            }
        }
    }
}
