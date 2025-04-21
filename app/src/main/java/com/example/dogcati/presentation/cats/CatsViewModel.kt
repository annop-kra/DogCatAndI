package com.example.dogcati.presentation.cats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.dogcati.domain.model.CatBreed
import com.example.dogcati.domain.usecase.GetCatBreedsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CatsViewModel(
    private val getCatBreedsUseCase: GetCatBreedsUseCase
) : ViewModel() {

    private val _catBreeds = MutableStateFlow<PagingData<CatBreed>>(PagingData.empty())
    val catBreeds: StateFlow<PagingData<CatBreed>> get() = _catBreeds

    init {
        loadCatBreeds()
    }

    private fun loadCatBreeds() {
        viewModelScope.launch {
            try {
                Log.d("CatsViewModel", "Fetching cat breeds...")
                getCatBreedsUseCase()
                    .cachedIn(viewModelScope)
                    .collectLatest { pagingData ->
                        _catBreeds.value = pagingData
                        Log.d("CatsViewModel", "Received new PagingData")
                    }
            } catch (e: Exception) {
                Log.e("CatsViewModel", "Error: ${e.message}", e)
            }
        }
    }
}
