package com.example.grabapp.data.api

import com.example.grabapp.data.model.payment.PayOSPaymentRequest
import com.example.grabapp.data.model.payment.PayOSPaymentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PayOSApi {

    @POST("payment")
    suspend fun createPaymentLink(
        @Body request: PayOSPaymentRequest
    ): Response<PayOSPaymentResponse>

}