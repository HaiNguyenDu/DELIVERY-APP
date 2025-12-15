package com.example.grabapp.domain.repository

import com.example.grabapp.data.model.user.UserResponse
import com.example.grabapp.domain.model.user.User
import com.example.grabapp.domain.model.user.UserAddress
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun updateUser(user: User)
    suspend fun getUserByIdFromSever(id:String): UserResponse?
    suspend fun sendFCM(fcm:String)
}
