package com.example.grabapp.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(phone: String, password: String): Result<String>
}