package com.example.dogcati.data.mapper

import com.example.dogcati.data.datasource.remote.response.CatBreed
import com.example.dogcati.domain.model.CatBreed as DomainCatBreed

fun CatBreed.toDomainCatBreed(): DomainCatBreed? {
    return if (breed != null) {
        DomainCatBreed(
            breed = breed,
            country = country ?: "",
            origin = origin ?: "",
            coat = coat ?: "",
            pattern = pattern ?: ""
        )
    } else {
        null
    }
}
