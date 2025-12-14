package com.example.grabapp.data.model.order

import com.example.grabapp.data.model.order.AddressInfo
import com.example.grabapp.domain.enum.PackageStatus
import com.google.gson.annotations.SerializedName
import java.util.Date

data class PackageItem(
    @SerializedName("id") val id: String = "",
    @SerializedName("weightKg") val weightKg: Double,
    @SerializedName("size") val packageSize: String,
    @SerializedName("codAmount") val codAmount: Double,
    @SerializedName("cod") val cod: Boolean,
    @SerializedName("payerType") val payerType: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String,
    @SerializedName("imageUrl") val imgUrl:String,
    @SerializedName("receiverAddress") val dropoffAddress: AddressInfo,
)

data class PackageItemResponse(
    @SerializedName("id") val id: String = "",
    @SerializedName("weightKg") val weightKg: Double,
    @SerializedName("packageSize") val packageSize: String,
    @SerializedName("codAmount") val codAmount: Double,
    @SerializedName("cod") val cod: Boolean,
    @SerializedName("payerType") val payerType: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String,
    @SerializedName("imageUrl") val imgUrl:String,
    @SerializedName("dropoffAddress") val dropoffAddress: AddressInfo,
    @SerializedName("statusNote") val statusNote: String?,
    @SerializedName("statusUpdatedAt") val statusUpdatedAt: String?,
    @SerializedName("packageStatus") val packageStatus: PackageStatus?
){
    override fun toString(): String {
        return "${packageSize}-$weightKg kg-${category}"
    }
}