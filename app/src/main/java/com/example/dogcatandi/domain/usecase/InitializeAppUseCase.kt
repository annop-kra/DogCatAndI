package com.example.dogcatandi.domain.usecase

import kotlinx.coroutines.delay

class InitializeAppUseCase {
    suspend operator fun invoke() {
        delay(2000)
    }
}
