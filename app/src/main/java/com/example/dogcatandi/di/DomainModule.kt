package com.example.dogcatandi.di

import com.example.dogcatandi.domain.usecase.GetCatBreedsUseCase
import com.example.dogcatandi.domain.usecase.GetDogImagesUseCase
import com.example.dogcatandi.domain.usecase.GetRandomUserUseCase
import com.example.dogcatandi.domain.usecase.InitializeAppUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { InitializeAppUseCase() }
    factory { GetDogImagesUseCase(get()) }
    factory { GetCatBreedsUseCase(get()) }
    factory { GetRandomUserUseCase(get()) }
}
