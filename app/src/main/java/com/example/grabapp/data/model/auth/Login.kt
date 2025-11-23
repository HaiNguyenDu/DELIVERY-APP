package com.example.grabapp.data.model.auth

data class LoginRequest(
    val phone: String,
    val password: String
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String
)