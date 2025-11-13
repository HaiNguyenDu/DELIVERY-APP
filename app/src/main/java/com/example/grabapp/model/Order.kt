package com.example.grabapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
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
    val notion: String?,
    val fragileGoods: Boolean,
    val isBusinessHours: Boolean,
    val orderType: OrderType,
    val goodsWeight: String
) : Parcelable {
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
                notion = "Gọi trước 10 phút",
                fragileGoods = true,
                isBusinessHours = true,
                orderType = OrderType.FRAGILE,
                goodsWeight = "2.5 kg"
            )
        }
    }
}

