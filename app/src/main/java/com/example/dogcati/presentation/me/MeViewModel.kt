package com.example.dogcati.presentation.me

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogcati.domain.usecase.GetRandomUserUseCase
import com.example.dogcati.domain.model.UserProfile
import kotlinx.coroutines.launch

class MeViewModel(
    private val getRandomUserUseCase: GetRandomUserUseCase
) : ViewModel() {

    private var isLoaded = false

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> get() = _userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun loadUserProfile(forceRefresh: Boolean = false) {
        if (isLoaded && !forceRefresh) return // ไม่โหลดซ้ำถ้ามีข้อมูลแล้ว

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = getRandomUserUseCase()
                _userProfile.value = user
                isLoaded = true
            } catch (e: Exception) {
                _error.value = "Failed to load user profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
