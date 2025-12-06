package com.example.grabapp.data.api

import com.example.grabapp.data.model.order.GetListOrderResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DriverApi {
    @GET("drivers/{id}")
    suspend fun getListOrders(
        @Path("id") id: String,
    ): Response<GetListOrderResponse>
}
