package com.example.grabapp.data.model.order

import com.example.grabapp.data.model.order.AddressInfo
import com.example.grabapp.data.model.order.OrderItem
import com.example.grabapp.data.model.order.PackageItem
import com.google.gson.annotations.SerializedName

class GetListOrderResponse(
    @SerializedName("content") val content: List<OrderItem>,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalElements") val totalElements: Int,
    @SerializedName("last") val isLastPage: Boolean,
    @SerializedName("number") val currentPage: Int
)

class OrderItemResponse(
    val id: String,
    val totalAmount:String,
    val status: String,
    val createdAt:String,
    val scheduledAt:String
)