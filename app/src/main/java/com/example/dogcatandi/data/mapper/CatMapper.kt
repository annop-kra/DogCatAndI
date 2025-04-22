package com.example.dogcatandi.data.mapper

import com.example.dogcatandi.data.datasource.remote.response.CatBreed
import com.example.dogcatandi.domain.model.CatBreed as DomainCatBreed

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
