package com.example.grabapp.data.model.user

data class UserResponse(
    val id: String,
    val phone: String,
    val fullName: String,
    val dob: String,
    val roles: List<String>,
    val enabled: Boolean,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val avatarUrl: String?,
)

data class UpdateProfileRequest(
    val fullName: String,
    val dob: String,
    val avatarUrl: String
)