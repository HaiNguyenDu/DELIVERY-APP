package com.example.grabapp.domain.model.order

import com.google.gson.annotations.SerializedName

data class OrderItem(
    @SerializedName("id") val id: String,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("status") val status: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("scheduledAt") val scheduledAt: String?,
    @SerializedName("pickupAddress") val pickupAddress: AddressInfo,
    @SerializedName("packages") val packages: List<PackageItem>,
    @SerializedName("shipperId") val shipperId: String?
)