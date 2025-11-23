package com.example.grabapp.domain.repository

import com.example.grabapp.domain.model.user.User
import com.example.grabapp.domain.model.user.UserAddress
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(): Flow<User?>
    suspend fun updateUser()
    suspend fun insertUser(user: User)
    fun getUserAddress(): Flow<UserAddress?>
    suspend fun insertUserAddress(address: UserAddress)
}