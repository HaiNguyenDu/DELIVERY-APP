package com.example.grabapp.data.api

import com.example.grabapp.data.model.order.CreateOrderRequest
import com.example.grabapp.data.model.order.CreateOrderResponse
import com.example.grabapp.data.model.order.GetListOrderResponse
import com.example.grabapp.data.model.order.OrderItem
import com.example.grabapp.data.model.order.OrderItemResponse
import com.example.grabapp.data.model.order.PriceRouteItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OderApi {

    @GET("order/")
    suspend fun getListOrders(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
    ): Response<GetListOrderResponse>

    @GET("order/{id}")
    suspend fun getDetailOrder(
        @Path("id") id: String,
    ): Response<OrderItemResponse>

    @POST("order/")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): Response<CreateOrderResponse>

    @POST("order/price-route")
    suspend fun getPriceRoute(
        @Body request: CreateOrderRequest
    ): Response<List<PriceRouteItem>>
}