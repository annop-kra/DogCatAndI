package com.example.dogcati.di

import com.example.dogcati.domain.usecase.GetCatBreedsUseCase
import com.example.dogcati.domain.usecase.GetRandomUserUseCase
import com.example.dogcati.domain.usecase.InitializeAppUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { InitializeAppUseCase() }
//    factory { GetDogImagesUseCase(get()) }
    factory { GetCatBreedsUseCase(get()) }
    factory { GetRandomUserUseCase(get()) }
}
