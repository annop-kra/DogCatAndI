package com.example.dogcatandi.presentation.cats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.dogcatandi.domain.usecase.GetCatBreedsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CatsViewModel(
    private val getCatBreedsUseCase: GetCatBreedsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CatsUiState>(CatsUiState.Loading)
    val uiState: StateFlow<CatsUiState> = _uiState.asStateFlow()

    init {
        loadCatBreeds()
    }

    fun loadCatBreeds() {
        viewModelScope.launch {
            _uiState.value = CatsUiState.Loading
             delay(100)
            try {
                getCatBreedsUseCase()
                    .cachedIn(viewModelScope)
                    .catch { e ->
                        println("Caught error in catch: ${e.message}")
                        _uiState.value = CatsUiState.Error("Failed to load cat breeds: ${e.message}")
                    }
                    .collectLatest { pagingData ->
                        println("Collected pagingData")
                        _uiState.value = CatsUiState.Success(pagingData)
                    }
            } catch (e: Exception) {
                _uiState.value = CatsUiState.Error("Failed to load cat breeds: ${e.message}")
            }
        }
    }
}
