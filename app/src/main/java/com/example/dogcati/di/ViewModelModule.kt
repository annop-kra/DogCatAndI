package com.example.dogcati.di

import com.example.dogcati.presentation.cats.CatsViewModel
import com.example.dogcati.presentation.me.MeViewModel
import com.example.dogcati.presentation.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { CatsViewModel(get()) }
    viewModel { MeViewModel(get()) }
}
