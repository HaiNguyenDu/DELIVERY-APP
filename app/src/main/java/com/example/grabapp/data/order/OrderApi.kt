package com.example.grabapp.data.order

import com.example.grabapp.data.model.OrderListResponse
import com.example.grabapp.data.model.OrderResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApi {
    @GET("order/")
    suspend fun getOrders(
        @Query("role") role: String,
        @Query("userId") userId: String
    ): Response<OrderListResponse>
    
    @GET("order/{orderId}")
    suspend fun getOrderById(
        @Path("orderId") orderId: String
    ): Response<OrderResponse>
}
