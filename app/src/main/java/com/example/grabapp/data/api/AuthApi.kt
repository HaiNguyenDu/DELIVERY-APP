package com.example.grabapp.data.api

import com.example.grabapp.data.model.auth.LoginRequest
import com.example.grabapp.data.model.auth.LoginResponse
import com.example.grabapp.data.model.auth.RefreshTokenRequest
import com.example.grabapp.data.model.auth.RefreshTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>
}