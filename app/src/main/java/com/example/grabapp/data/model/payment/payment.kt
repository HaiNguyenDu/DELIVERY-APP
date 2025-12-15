package com.example.grabapp.data.model.payment

import com.google.gson.annotations.SerializedName

data class PayOSPaymentRequest(
    @SerializedName("orderCode")
    val orderCode: Long,

    @SerializedName("amount")
    val amount: Long,

    @SerializedName("description")
    val description: String,

    @SerializedName("cancelUrl")
    val cancelUrl: String,

    @SerializedName("returnUrl")
    val returnUrl: String,

    @SerializedName("expiredAt")
    val expiredAt: Long? = null,
)

data class PayOSPaymentResponse(
    @SerializedName("paymentId")
    val paymentId: Long? = null,

    @SerializedName("orderCode")
    val orderCode: Long? = null,

    @SerializedName("amount")
    val amount: Long? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("checkoutUrl")
    val checkoutUrl: String? = null,

    @SerializedName("qrCode")
    val qrCode: String? = null,

    @SerializedName("paymentLinkId")
    val paymentLinkId: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("expiredAt")
    val expiredAt: Long? = null,

    @SerializedName("payosData")
    val payosData: Map<String, Any>? = null
)