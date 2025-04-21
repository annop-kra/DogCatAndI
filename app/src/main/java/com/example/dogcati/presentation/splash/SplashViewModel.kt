package com.example.dogcati.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogcati.domain.usecase.InitializeAppUseCase
import kotlinx.coroutines.launch

class SplashViewModel(
    private val initializeAppUseCase: InitializeAppUseCase
) : ViewModel() {

    private val _isInitialized = MutableLiveData<Boolean>()
    val isInitialized: LiveData<Boolean> get() = _isInitialized

    fun initializeApp() {
        viewModelScope.launch {
            initializeAppUseCase()
            _isInitialized.value = true
        }
    }
}
