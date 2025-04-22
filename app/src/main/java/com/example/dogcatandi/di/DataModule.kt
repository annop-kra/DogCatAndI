package com.example.dogcatandi.di

import com.example.dogcatandi.BuildConfig
import com.example.dogcatandi.data.datasource.remote.CatApiService
import com.example.dogcatandi.data.datasource.remote.CatRemoteDataSource
import com.example.dogcatandi.data.datasource.remote.DogApiService
import com.example.dogcatandi.data.datasource.remote.DogRemoteDataSource
import com.example.dogcatandi.data.datasource.remote.UserApiService
import com.example.dogcatandi.data.datasource.remote.UserRemoteDataSource
import com.example.dogcatandi.data.mapper.DateAdapter
import com.example.dogcatandi.data.mapper.PostcodeAdapter
import com.example.dogcatandi.data.repository.CatRepositoryImpl
import com.example.dogcatandi.data.repository.DogRepositoryImpl
import com.example.dogcatandi.data.repository.UserRepositoryImpl
import com.example.dogcatandi.domain.repository.CatRepository
import com.example.dogcatandi.domain.repository.DogRepository
import com.example.dogcatandi.domain.repository.UserRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

val dataModule = module {
    // Moshi
    single {
        Moshi.Builder()
            .add(DateAdapter)
            .add(PostcodeAdapter)
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    // Retrofit Instances
    single(named("dogRetrofit")) { provideDogRetrofit(get()) }
    single(named("catRetrofit")) { provideCatRetrofit(get()) }
    single(named("userRetrofit")) { provideUserRetrofit(get()) }

    // API Services
    single { get<Retrofit>(named("dogRetrofit")).create(DogApiService::class.java) }
    single { get<Retrofit>(named("catRetrofit")).create(CatApiService::class.java) }
    single { get<Retrofit>(named("userRetrofit")).create(UserApiService::class.java) }

    // Repositories
    single<DogRepository> { DogRepositoryImpl(get()) }
    single<CatRepository> { CatRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }

    single { DogRemoteDataSource(get()) }
    single { CatRemoteDataSource(get()) }
    single { UserRemoteDataSource(get()) }
}

private fun provideDogRetrofit(moshi: Moshi): Retrofit {
    return provideRetrofit(BuildConfig.DOG_API_BASE_URL, moshi)
}

private fun provideCatRetrofit(moshi: Moshi): Retrofit {
    return provideRetrofit(BuildConfig.CAT_API_BASE_URL, moshi)
}

private fun provideUserRetrofit(moshi: Moshi): Retrofit {
    return provideRetrofit(BuildConfig.USER_API_BASE_URL, moshi)
}

private fun provideRetrofit(baseUrl: String, moshi: Moshi): Retrofit {
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        })
        .build()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
}
