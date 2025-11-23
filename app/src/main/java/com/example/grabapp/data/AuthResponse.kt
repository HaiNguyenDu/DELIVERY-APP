package com.example.grabapp.data

data class AuthResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val tokenType: String?
)
