package com.example.dogcatandi.domain.manager

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface LoadingManager {
    val isLoading: Flow<Boolean>
    fun setLoading(isLoading: Boolean)
}

class LoadingManagerImpl : LoadingManager {
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: Flow<Boolean> get() = _isLoading

    override fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}
