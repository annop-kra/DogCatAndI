package com.example.dogcatandi.data.mapper

import com.example.dogcatandi.data.datasource.remote.response.User
import com.example.dogcatandi.data.datasource.remote.response.UserResponse
import com.example.dogcatandi.domain.model.UserProfile

fun UserResponse.toDomainUserProfile(): UserProfile? {
    return results?.firstOrNull()?.toDomainUserProfile()
}

fun User.toDomainUserProfile(): UserProfile? {
    return if (name?.first != null && name.last != null) {
        UserProfile(
            title = name.title ?: "",
            firstName = name.first,
            lastName = name.last,
            dateOfBirth = dob?.date?.takeIf { it.isNotBlank() } ?: "", // ใช้วันที่ที่แปลงแล้ว
            age = dob?.age ?: 0,
            gender = gender ?: "",
            nationality = nat ?: "",
            phone = phone ?: "",
            address = location?.let {
                "${it.street?.number ?: ""} ${it.street?.name ?: ""}, ${it.city ?: ""}, ${it.state ?: ""}, ${it.country ?: ""}, ${it.postcode ?: ""}"
            }?.trim() ?: "",
            profileImageUrl = picture?.large ?: ""
        )
    } else {
        null
    }
}
