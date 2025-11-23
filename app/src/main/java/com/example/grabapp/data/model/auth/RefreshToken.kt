package com.example.grabapp.data.model.auth

data class RefreshTokenRequest(
    val refreshToken: String
)

data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String
)