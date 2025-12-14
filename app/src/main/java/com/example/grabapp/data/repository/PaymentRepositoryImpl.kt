package com.example.grabapp.data.repository

import com.example.grabapp.data.api.PayOSApi
import com.example.grabapp.data.model.payment.PayOSPaymentRequest
import com.example.grabapp.data.model.payment.PayOSPaymentResponse
import com.example.grabapp.domain.repository.PaymentRepository
import com.example.grabapp.network.ApiProvider

class PaymentRepositoryImpl : PaymentRepository {
    override suspend fun createPayment(request: PayOSPaymentRequest): Result<PayOSPaymentResponse> {
        val api = ApiProvider.getInstance().getNoAuthApi(PayOSApi::class.java)
        return try {
            val resp = api.createPaymentLink(request)
            if (resp.isSuccessful) {
                val body = resp.body()
                if (body != null) Result.success(body) else Result.failure(Exception("Empty body"))
            } else {
                val err = resp.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}