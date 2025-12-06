package com.example.grabapp.domain.repository

import com.example.grabapp.data.model.payment.PayOSPaymentRequest
import com.example.grabapp.data.model.payment.PayOSPaymentResponse


interface PaymentRepository {

    suspend fun createPayment(request: PayOSPaymentRequest): Result<PayOSPaymentResponse>
}