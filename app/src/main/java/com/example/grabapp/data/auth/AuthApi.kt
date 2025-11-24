package com.example.grabapp.data.auth

import com.example.grabapp.data.model.AuthRequest
import com.example.grabapp.data.model.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>
}
