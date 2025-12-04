package com.example.grabapp.data.auth

import com.example.grabapp.data.model.DriverRegisterRequest
import com.example.grabapp.data.model.DriverRegisterResponse
import com.example.grabapp.data.model.UpdateDriverLocationRequest
import com.example.grabapp.data.model.UpdateDriverStatusRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.PUT

interface DriverApi {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("auth/drivers")
    suspend fun register(@Body request: DriverRegisterRequest): Response<DriverRegisterResponse>

    @GET("auth/drivers/{id}")
    suspend fun getDriver(
        @Path("id") id: String
    ): Response<DriverRegisterResponse>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("auth/drivers/update-status")
    suspend fun updateDriverStatus(@Body request: UpdateDriverStatusRequest): Response<Unit>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @PUT("auth/drivers/location")
    suspend fun updateDriverLocation(@Body request: UpdateDriverLocationRequest): Response<ResponseBody>
}
