package com.example.grabapp.domain.repository

import com.example.grabapp.data.model.auth.LoginResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(phone: String, password: String): Result<LoginResponse>
}