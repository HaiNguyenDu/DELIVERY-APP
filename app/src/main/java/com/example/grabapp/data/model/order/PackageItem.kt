package com.example.grabapp.data.model.order

import com.example.grabapp.data.model.order.AddressInfo
import com.google.gson.annotations.SerializedName

data class PackageItem(
    @SerializedName("id") val id: String,
    @SerializedName("weightKg") val weightKg: Double,
    @SerializedName("packageSize") val packageSize: String,
    @SerializedName("deliveryFee") val deliveryFee: Double,
    @SerializedName("codFee") val codFee: Double,
    @SerializedName("payerType") val payerType: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String,
    @SerializedName("dropoffAddress") val dropoffAddress: AddressInfo
)