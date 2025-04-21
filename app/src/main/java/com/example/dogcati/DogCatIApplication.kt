package com.example.dogcati

import android.app.Application
import com.example.dogcati.di.dataModule
import com.example.dogcati.di.domainModule
import com.example.dogcati.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class DogCatIApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DogCatIApplication)
            modules(
                dataModule, domainModule, viewModelModule
            )
        }
    }
}