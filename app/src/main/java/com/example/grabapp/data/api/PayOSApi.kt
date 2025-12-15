package com.example.grabapp.data.api

import com.example.grabapp.data.model.payment.PayOSPaymentRequest
import com.example.grabapp.data.model.payment.PayOSPaymentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PayOSApi {

    @POST("payment")
    suspend fun createPaymentLink(
        @Body request: PayOSPaymentRequest
    ): Response<PayOSPaymentResponse>

    @GET("payment/{orderCode}")
    suspend fun getPaymentInfo(
        @Path("orderCode") orderCode: Long
    ): Response<PayOSPaymentResponse>

}