package com.example.grabapp.data.model

import com.google.gson.annotations.SerializedName

data class UpdatePackageStatusRequest(
    @SerializedName("status")
    val status: String,
    @SerializedName("note")
    val note: String?
)
