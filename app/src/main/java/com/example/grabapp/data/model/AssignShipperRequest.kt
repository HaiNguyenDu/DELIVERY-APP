package com.example.grabapp.data.model

import com.google.gson.annotations.SerializedName

data class AssignShipperRequest(
    @SerializedName("orderId")
    val orderId: String
)
