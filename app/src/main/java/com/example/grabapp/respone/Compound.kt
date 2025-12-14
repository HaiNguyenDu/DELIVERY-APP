package com.example.grabapp.respone

import com.google.gson.annotations.SerializedName

data class Compound(
    @SerializedName("district")
    val district: String? = null,

    @SerializedName("commune")
    val ward: String? = null,

    @SerializedName("province")
    val province: String? = null
)