package com.example.grabapp.domain.model.user

data class User(
    val id: String,
    val phone: String,
    val fullName: String,
    val avatarUrl: String?,
    val isActive: Boolean,
    val role: Int,
    val date: String?,
    val isAvailable: Boolean
)
