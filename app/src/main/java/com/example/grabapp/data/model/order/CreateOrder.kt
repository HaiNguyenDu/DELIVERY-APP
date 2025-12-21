package com.example.grabapp.data.model.order

import com.example.grabapp.data.model.payment.PayOSPaymentResponse

data class CreateOrderRequest(
    val pickupAddress: AddressInfo,
    val packages: List<PackageItem>,
    val customerNote: String? = null,
    val voucherCode: String? = null,
    val fragile: Boolean? = null,
    val thermalBag: Boolean? = null,
    val returnToPickupWhenCod: Boolean? = null,
    val scheduledAt: String? = null,
    val paymentMethod: String? = null
)

data class CreateOrderResponse(
    val orderId: String,
    val totalAmount: Double,
    val currency: String,
    val status: String,
    val paymentResponse: PayOSPaymentResponse?
)