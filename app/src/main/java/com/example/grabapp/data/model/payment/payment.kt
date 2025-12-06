package com.example.grabapp.data.model.payment

data class PayOSPaymentRequest(
    val orderCode: Long,
    val amount: Int,
    val description: String,
    val cancelUrl: String,
    val returnUrl: String,
    val expiredAt: Long? = null,
    val buyer: BuyerInfo? = null,
    val items: List<PayOSItem>? = null,
    val invoice: InvoiceInfo? = null
)

data class BuyerInfo(val name: String? = null, val phone: String? = null, val email: String? = null)
data class PayOSItem(val name: String, val quantity: Int, val price: Int)
data class InvoiceInfo(val orderCode: String? = null, val note: String? = null)

data class PayOSPaymentResponse(val code: String, val desc: String, val result: PayOSResult?)
data class PayOSResult(val checkoutUrl: String?, val transaction: String?, val orderCode: Long?)