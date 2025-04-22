package com.example.dogcatandi.di

import com.example.dogcatandi.presentation.cats.CatsViewModel
import com.example.dogcatandi.presentation.dogs.DogsViewModel
import com.example.dogcatandi.presentation.me.MeViewModel
import com.example.dogcatandi.presentation.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { DogsViewModel(get()) }
    viewModel { CatsViewModel(get()) }
    viewModel { MeViewModel(get()) }
}
