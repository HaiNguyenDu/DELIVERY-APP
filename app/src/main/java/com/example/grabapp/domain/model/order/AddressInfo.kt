package com.example.grabapp.domain.model.order

import com.google.gson.annotations.SerializedName

data class AddressInfo(
    @SerializedName("detail") val detail: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)