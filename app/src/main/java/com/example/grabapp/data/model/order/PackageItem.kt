package com.example.grabapp.data.model.order

import com.example.grabapp.data.model.order.AddressInfo
import com.google.gson.annotations.SerializedName

data class PackageItem(
    @SerializedName("id") val id: String = "",
    @SerializedName("weightKg") val weightKg: Double,
    @SerializedName("size") val packageSize: String,
    @SerializedName("deliveryFee") val deliveryFee: Double,
    @SerializedName("codAmount") val codAmount: Double,
    @SerializedName("cod") val cod: Boolean,
    @SerializedName("payerType") val payerType: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String,
    @SerializedName("imageUrl") val imgUrl:String,
    @SerializedName("receiverAddress") val dropoffAddress: AddressInfo
)