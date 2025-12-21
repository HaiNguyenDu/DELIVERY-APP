package com.example.grabapp.data.api

import com.example.grabapp.data.model.user.UpdateProfileRequest
import com.example.grabapp.data.model.user.UserResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {

    @GET("auth/users/{id}")
    suspend fun getUserById(
        @Path("id") id: String
    ): Response<UserResponse>

    @PUT("auth/users/me")
    suspend fun updateUser(
        @Body updateProfileRequest: UpdateProfileRequest
    ): Response<Unit>
}