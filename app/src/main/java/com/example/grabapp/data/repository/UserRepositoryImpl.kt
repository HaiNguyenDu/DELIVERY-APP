package com.example.grabapp.data.repository

import android.util.Log
import com.example.grabapp.data.api.FCMApi
import com.example.grabapp.data.api.UserApi
import com.example.grabapp.data.model.auth.FcmRequest
import com.example.grabapp.data.model.user.UpdateProfileRequest
import com.example.grabapp.data.model.user.UserResponse
import com.example.grabapp.domain.model.user.User
import com.example.grabapp.domain.repository.UserRepository
import com.example.grabapp.network.ApiProvider

class UserRepositoryImpl() : UserRepository {

    val fcmApi = ApiProvider.getInstance().getAuthRequiredApi(FCMApi::class.java)
    val userApi = ApiProvider.getInstance().getAuthRequiredApi(UserApi::class.java)
    override fun updateUser(user: User) {
        try {
            val userRequest = UpdateProfileRequest(
                user.fullName,
                user.date ?: "",
                user.avatarUrl ?: ""
            )

            userApi.updateUser(userRequest)
        } catch (e: Exception) {
            Log.e("GrabAppError", e.message.toString())
        }
    }

    override suspend fun getUserByIdFromSever(id: String): UserResponse? {
        try {
            val response = userApi.getUserById(id)
            if (response.isSuccessful)
                return response.body()
        } catch (e: Exception) {
            Log.e("GrabAppError", e.message.toString())
        }
        return null
    }

    override suspend fun sendFCM(fcm: String) {
        try {
            fcmApi.sendFcm(FcmRequest(fcm))
        } catch (e: Exception) {
        }
    }

}
