package com.example.grabapp.data.model.order
data class CreateOrderRequest(
    val pickupAddress: AddressInfo,
    val packages: List<PackageItem>,
    val customerNote: String?,
    val fragile: Boolean
)

data class CreateOrderResponse(
    val orderId: String,
    val totalAmount: Double,
    val currency: String,
    val status: String
)