package com.example.grabapp.data.repository

import com.example.grabapp.data.local.user.UserDao
import com.example.grabapp.domain.repository.UserRepository
import com.example.grabapp.domain.model.user.User
import com.example.grabapp.domain.model.user.UserAddress
import com.example.grabapp.domain.model.user.toDomain
import com.example.grabapp.domain.model.user.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(private val dao: UserDao) : UserRepository {
    override fun getUser(): Flow<User?> {
        return dao.getUser().map { it?.toDomain() }

    }

    override suspend fun updateUser() {

    }

    override suspend fun insertUser(user: User) {
        dao.insertUser(user.toEntity())
    }

    override fun getUserAddress(): Flow<UserAddress?> = dao.getAddresses().map { it?.toDomain() }

    override suspend fun insertUserAddress(address: UserAddress) {
        dao.saveAddresses(address.toEntity())
    }
}
