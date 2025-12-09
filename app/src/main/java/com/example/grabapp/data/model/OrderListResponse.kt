package com.example.grabapp.data.model

import com.google.gson.annotations.SerializedName

data class OrderListResponse(
    @SerializedName("content")
    val content: List<OrderResponse>,
    @SerializedName("pageable")
    val pageable: Pageable,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("totalElements")
    val totalElements: Int,
    @SerializedName("totalPages")
    val totalPages: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("number")
    val number: Int,
    @SerializedName("first")
    val first: Boolean,
    @SerializedName("numberOfElements")
    val numberOfElements: Int,
    @SerializedName("sort")
    val sort: SortInfo,
    @SerializedName("empty")
    val empty: Boolean
)

data class OrderResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("totalAmount")
    val totalAmount: Long,
    @SerializedName("status")
    val status: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("scheduledAt")
    val scheduledAt: String?,
    @SerializedName("pickupAddress")
    val pickupAddress: AddressInfo,
    @SerializedName("packages")
    val packages: List<PackageInfo>,
    @SerializedName("shipperId")
    val shipperId: String,
    @SerializedName("priceAndRoutes")
    val priceAndRoutes: List<PriceAndRoute>
)

data class AddressInfo(
    @SerializedName("detail")
    val detail: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("note")
    val note: String?,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)

data class PackageInfo(
    @SerializedName("id")
    val id: String,
    @SerializedName("weightKg")
    val weightKg: Double,
    @SerializedName("packageSize")
    val packageSize: String,
    @SerializedName("deliveryFee")
    val deliveryFee: Long,
    @SerializedName("codFee")
    val codFee: Long,
    @SerializedName("payerType")
    val payerType: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("packageStatus")
    val packageStatus: String?,
    @SerializedName("statusNote")
    val statusNote: String?,
    @SerializedName("statusUpdatedAt")
    val statusUpdatedAt: String?,
    @SerializedName("dropoffAddress")
    val dropoffAddress: AddressInfo
)

data class PriceAndRoute(
    @SerializedName("price")
    val price: Long,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("routeIndex")
    val routeIndex: Int,
    @SerializedName("packageIndex")
    val packageIndex: Int,
    @SerializedName("distance")
    val distance: Long,
    @SerializedName("estimatedDuration")
    val estimatedDuration: Long
)

data class Pageable(
    @SerializedName("pageNumber")
    val pageNumber: Int,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("sort")
    val sort: SortInfo,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("paged")
    val paged: Boolean,
    @SerializedName("unpaged")
    val unpaged: Boolean
)

data class SortInfo(
    @SerializedName("empty")
    val empty: Boolean,
    @SerializedName("sorted")
    val sorted: Boolean,
    @SerializedName("unsorted")
    val unsorted: Boolean
)

