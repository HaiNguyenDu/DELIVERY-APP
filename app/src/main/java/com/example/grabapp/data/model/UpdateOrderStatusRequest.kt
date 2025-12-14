package com.example.grabapp.data.model

import com.google.gson.annotations.SerializedName

data class UpdateOrderStatusRequest(
    @SerializedName("newStatus")
    val newStatus: String,
    @SerializedName("reasonNote")
    val reasonNote: String? = null
)
