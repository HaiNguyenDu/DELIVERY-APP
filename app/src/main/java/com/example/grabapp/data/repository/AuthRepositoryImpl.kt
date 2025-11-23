package com.example.grabapp.data.repository

import com.example.grabapp.data.model.auth.LoginRequest
import com.example.grabapp.domain.repository.AuthRepository
import com.example.grabapp.network.ApiProvider

class AuthRepositoryImpl : AuthRepository {
    override suspend fun login(
        phone: String,
        password: String
    ): Result<String> {
        return try {
            val response = ApiProvider.getAuthApi().login(LoginRequest(phone, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.accessToken)
            }
            Result.failure(Exception("login failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}