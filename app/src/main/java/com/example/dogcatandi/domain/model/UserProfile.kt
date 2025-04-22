package com.example.dogcatandi.domain.model

data class UserProfile(
    val title: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val age: Int,
    val gender: String,
    val nationality: String,
    val phone: String,
    val address: String,
    val profileImageUrl: String
)
