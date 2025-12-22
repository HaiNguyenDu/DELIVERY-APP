package com.example.grabapp.data.order

import com.example.grabapp.data.model.AssignShipperRequest
import com.example.grabapp.data.model.OrderListResponse
import com.example.grabapp.data.model.OrderResponse
import com.example.grabapp.data.model.PackageInfo
import com.example.grabapp.data.model.UpdateOrderStatusRequest
import com.example.grabapp.data.model.UpdatePackageStatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
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
    
    @POST("order/assign-shipper")
    suspend fun assignShipper(
        @Body request: AssignShipperRequest
    ): Response<OrderResponse>
    
    @PATCH("order/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: String,
        @Body request: UpdateOrderStatusRequest
    ): Response<OrderResponse>
    
    @PATCH("order/{packageId}/package-status")
    suspend fun updatePackageStatus(
        @Path("packageId") packageId: String,
        @Body request: UpdatePackageStatusRequest
    ): Response<PackageInfo>
    
    @GET("order/{driverId}/rating")
    suspend fun getDriverRating(
        @Path("driverId") driverId: String
    ): Response<com.example.grabapp.data.model.RatingResponse>
}
