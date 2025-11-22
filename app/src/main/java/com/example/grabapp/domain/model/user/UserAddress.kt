package com.example.grabapp.domain.model.user

data class UserAddress(
    val id: String,
    val userId: String,
    val detail: String,
    val phone: String,
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean
)