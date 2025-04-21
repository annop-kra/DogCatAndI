package com.example.dogcati.data.mapper

import com.example.dogcati.data.datasource.remote.response.User
import com.example.dogcati.data.datasource.remote.response.UserResponse
import com.example.dogcati.domain.model.UserProfile

fun UserResponse.toUserProfile(): UserProfile? {
    return results?.firstOrNull()?.toUserProfile()
}

fun User.toUserProfile(): UserProfile? {
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
