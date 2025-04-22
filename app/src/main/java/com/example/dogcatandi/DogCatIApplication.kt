package com.example.dogcatandi

import android.app.Application
import com.example.dogcatandi.di.dataModule
import com.example.dogcatandi.di.domainModule
import com.example.dogcatandi.di.viewModelModule
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class DogCatIApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        startKoin {
            androidContext(this@DogCatIApplication)
            modules(
                dataModule, domainModule, viewModelModule
            )
        }
    }
}