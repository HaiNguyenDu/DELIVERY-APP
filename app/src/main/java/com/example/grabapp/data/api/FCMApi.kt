package com.example.grabapp.data.api

import com.example.grabapp.data.model.auth.FcmRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FCMApi {
    @POST("auth/users/update-fcmtoken")
    suspend fun sendFcm(
        @Body body: FcmRequest
    ): Response<String>
}