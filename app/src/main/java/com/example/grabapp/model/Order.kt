package com.example.grabapp.model

data class Order(
    val orderId: String,
    val pickerName: String,
    val pickerAddress: String,
    val deliveryName: String,
    val deliveryAddress: String,
    val distance: String,
    val estimatedTime: String,
    val income: Long,
    val hasCOD: Boolean,
    val isNotion: Boolean,
    val fragileGoods: Boolean
) {
    companion object {
        fun getMockOrder(): Order {
            return Order(
                orderId = "MĐH001234",
                pickerName = "Nguyễn Văn A",
                pickerAddress = "32 Trần Kế Xương, Hải Châu 1, Đà Nẵng",
                deliveryName = "Trần Thị B",
                deliveryAddress = "456 Nguyễn Huệ, Quận 1, TP.HCM",
                distance = "3.2 km",
                estimatedTime = "15 phút",
                income = 75000L,
                hasCOD = false,
                isNotion = true,
                fragileGoods = true
            )
        }
    }
}

