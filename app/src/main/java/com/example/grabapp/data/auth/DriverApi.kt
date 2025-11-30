package com.example.grabapp.data.auth

import com.example.grabapp.data.model.DriverRegisterRequest
import com.example.grabapp.data.model.DriverRegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface DriverApi {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("auth/drivers")
    suspend fun register(@Body request: DriverRegisterRequest): Response<DriverRegisterResponse>
}
