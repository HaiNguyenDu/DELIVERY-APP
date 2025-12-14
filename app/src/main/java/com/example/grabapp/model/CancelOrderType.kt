package com.example.grabapp.model

enum class CancelOrderType(
    val cancelName: String
) {
    PICKUP_ATTEMPT_FAILED("Không liên lạc được người gửi"),
    DELIVERY_ATTEMPT_FAILED("Không liên lạc được người nhận"),
    DRIVER_ISSUE_REPORTED("Tài xế gặp sự cố")
}
