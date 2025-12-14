package com.example.grabapp.data.model.order

import OrderStatus
import com.example.grabapp.domain.enum.PackageStatus
import com.google.gson.annotations.SerializedName
import java.util.Date

data class OrderItem(
    @SerializedName("id") val id: String,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("status") val status: OrderStatus,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("scheduledAt") val scheduledAt: String?,
    @SerializedName("pickupAddress") val pickupAddress: AddressInfo,
    @SerializedName("packages") val packages: List<PackageItem>,
    @SerializedName("shipperId") val shipperId: String?,
)

data class OrderItemResponse(
    @SerializedName("id") val id: String,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("status") val status: OrderStatus,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("scheduledAt") val scheduledAt: String?,
    @SerializedName("pickupAddress") val pickupAddress: AddressInfo,
    @SerializedName("packages") val packages: List<PackageItemResponse>,
    @SerializedName("shipperId") val shipperId: String?,
    @SerializedName("priceAndRoutes") val priceAndRoutes: List<PriceRouteItem>?
)