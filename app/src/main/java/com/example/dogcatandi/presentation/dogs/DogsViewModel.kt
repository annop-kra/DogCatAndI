package com.example.dogcatandi.presentation.dogs

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogcatandi.domain.usecase.GetDogImagesUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime


class DogsViewModel(
    private val getDogImagesUseCase: GetDogImagesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DogsUiState>(DogsUiState.Loading)
    val uiState: StateFlow<DogsUiState> = _uiState.asStateFlow()

    init {
        loadDogImagesConcurrently()
    }

    fun loadDogImagesConcurrently() {
        viewModelScope.launch {
            _uiState.value = DogsUiState.Loading
            try {
                val results = getDogImagesUseCase(3)
                val images = results.mapIndexedNotNull { index, result ->
                    result.fold(
                        onSuccess = { url ->
                            DogImage(url, LocalDateTime.now())
                        },
                        onFailure = { error ->
                            null
                        }
                    )
                }
                if (images.isNotEmpty()) {
                    _uiState.value = DogsUiState.Success(images)
                } else {
                    _uiState.value = DogsUiState.Error("Failed to load dog images")
                }
            } catch (e: Exception) {
                _uiState.value = DogsUiState.Error("Failed to load dog images: ${e.message}")
            }
        }
    }

    fun loadDogImagesSequentially() {
        viewModelScope.launch {
            _uiState.value = DogsUiState.Loading
            try {
                val currentSecond = LocalDateTime.now().second % 10
                val delayMillis = if (currentSecond < 5) 2000L else 3000L

                val images = mutableListOf<DogImage?>()
                repeat(3) { images.add(null) }
                _uiState.value = DogsUiState.Success(images.toList())

                repeat(3) { index ->
                    try {
                        val result = getDogImagesUseCase(1).first()
                        result.fold(
                            onSuccess = { url ->
                                images[index] = DogImage(url, LocalDateTime.now())
                                _uiState.value = DogsUiState.Success(images.toList())
                            },
                            onFailure = { error ->
                                Log.e("DogsViewModel", "Error fetching image ${index + 1}", error)
                            }
                        )
                    } catch (e: Exception) {
                        Log.e("DogsViewModel", "Exception fetching image ${index + 1}", e)
                    }

                    if (index < 2) {
                        delay(delayMillis)
                    }
                }

                if (images.all { it == null }) {
                    _uiState.value = DogsUiState.Error("Failed to load any dog images")
                }
            } catch (e: Exception) {
                _uiState.value = DogsUiState.Error("Failed to load dog images: ${e.message}")
            }
        }
    }
}
