package com.example.grabapp.data.repository

import android.util.Log
import com.example.grabapp.data.api.FCMApi
import com.example.grabapp.data.local.user.UserDao
import com.example.grabapp.domain.repository.UserRepository
import com.example.grabapp.domain.model.user.User
import com.example.grabapp.domain.model.user.UserAddress
import com.example.grabapp.domain.model.user.toDomain
import com.example.grabapp.domain.model.user.toEntity
import com.example.grabapp.network.ApiProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(private val dao: UserDao) : UserRepository {

    val fcmApi = ApiProvider.getInstance().getAuthRequiredApi(FCMApi::class.java)

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
    fun sendFCM(fcm: String) {
//        try {
//            val response  = fcmApi.sendFcm(fcm)
//            if(response.isSuccessful)
//            {
//
//            }
//        } catch (e: Exception) {
//            Log.e("fail token","sssssssssssssssssssssssssssssssssss " +e.message)
//        }
    }

}
