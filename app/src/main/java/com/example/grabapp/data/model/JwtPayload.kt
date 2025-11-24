package com.example.grabapp.data.model

import com.google.gson.annotations.SerializedName

data class JwtPayload(
    @SerializedName("sub")
    val sub: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("roles")
    val roles: List<String>?,
    @SerializedName("iat")
    val iat: Long?,
    @SerializedName("exp")
    val exp: Long?
)
