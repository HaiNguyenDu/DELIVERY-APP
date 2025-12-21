package com.example.grabapp.domain.repository

import com.example.grabapp.data.model.user.UserResponse
import com.example.grabapp.domain.model.user.User

interface UserRepository {

    suspend fun updateUser(user: User): Result<Boolean>
    suspend fun getUserByIdFromSever(id: String): UserResponse?
    suspend fun sendFCM(fcm: String)
}
