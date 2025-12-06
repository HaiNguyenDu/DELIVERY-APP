package com.example.grabapp.data.model.order

import com.google.gson.annotations.SerializedName

data class AddressInfo(
    @SerializedName("detail") val detail: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("phone") val phone: String = "",
    @SerializedName("latitude") val latitude: Double = 0.0,
    @SerializedName("longitude") val longitude: Double = 0.0,
    @SerializedName("id") val note: String = ""
)