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
    val orderState: OrderState,
    val goodsWeight: String,
    val orderTime: String
) : Parcelable {
    companion object {
        fun getMockOrder(): Order {
            return Order(
                orderId = "MĐH001234",
                pickerName = "Nguyễn Văn A",
                pickerAddress = "54 Nguyễn Lương Bằng, Hòa Khánh Bắc, Liên Chiểu, Đà Nẵng",
                deliveryName = "Trần Thị B",
                deliveryAddress = "Chợ Hòa Khánh, Âu Cơ, Hòa Khánh Bắc, Liên Chiểu, Đà Nẵng",
                distance = "800m",
                estimatedTime = "10 phút",
                income = 75000L,
                hasCOD = false,
                notion = "Gọi trước 10 phút",
                fragileGoods = true,
                isBusinessHours = true,
                orderType = OrderType.FRAGILE,
                orderState = OrderState.DELIVERING,
                goodsWeight = "2.5 kg",
                orderTime = "02/11/2025 05:20"
            )
        }

        fun getMockOrders(): List<Order> {
            return listOf(
                Order(
                    orderId = "MĐH001235",
                    pickerName = "Lê Thị C",
                    pickerAddress = "12 Lý Thường Kiệt, Hà Nội",
                    deliveryName = "Phạm Văn D",
                    deliveryAddress = "85 Nguyễn Văn Linh, Đà Nẵng",
                    distance = "5.8 km",
                    estimatedTime = "25 phút",
                    income = 95000L,
                    hasCOD = true,
                    notion = "Giao trong giờ hành chính",
                    fragileGoods = false,
                    isBusinessHours = true,
                    orderType = OrderType.FOOD,
                    orderState = OrderState.DELIVERING,
                    goodsWeight = "1.2 kg",
                    orderTime = "02/11/2025 08:15"
                ),
                Order(
                    orderId = "MĐH001236",
                    pickerName = "Trần Văn E",
                    pickerAddress = "23 Nguyễn Công Trứ, Huế",
                    deliveryName = "Ngô Thị F",
                    deliveryAddress = "115 Hai Bà Trưng, Huế",
                    distance = "2.1 km",
                    estimatedTime = "10 phút",
                    income = 55000L,
                    hasCOD = false,
                    notion = null,
                    fragileGoods = false,
                    isBusinessHours = true,
                    orderType = OrderType.FOOD,
                    orderState = OrderState.DELIVERED,
                    goodsWeight = "0.8 kg",
                    orderTime = "02/11/2025 10:30"
                ),
                Order(
                    orderId = "MĐH001237",
                    pickerName = "Phan Văn G",
                    pickerAddress = "221 Lê Duẩn, Đà Nẵng",
                    deliveryName = "Vũ Thị H",
                    deliveryAddress = "30 Nguyễn Tất Thành, Đà Nẵng",
                    distance = "4.0 km",
                    estimatedTime = "18 phút",
                    income = 80000L,
                    hasCOD = true,
                    notion = "Hàng dễ vỡ, cẩn thận",
                    fragileGoods = true,
                    isBusinessHours = false,
                    orderType = OrderType.FRAGILE,
                    orderState = OrderState.CANCELED,
                    goodsWeight = "3.0 kg",
                    orderTime = "02/11/2025 14:45"
                ),
                Order(
                    orderId = "MĐH001238",
                    pickerName = "Đinh Thị I",
                    pickerAddress = "92 Nguyễn Văn Cừ, Hà Nội",
                    deliveryName = "Lưu Văn K",
                    deliveryAddress = "15 Phan Chu Trinh, Hà Nội",
                    distance = "1.8 km",
                    estimatedTime = "8 phút",
                    income = 42000L,
                    hasCOD = false,
                    notion = "Liên hệ trước khi giao",
                    fragileGoods = false,
                    isBusinessHours = true,
                    orderType = OrderType.CLOTHING,
                    orderState = OrderState.DELIVERED,
                    goodsWeight = "0.5 kg",
                    orderTime = "02/11/2025 16:20"
                ),
                Order(
                    orderId = "MĐH001239",
                    pickerName = "Bùi Văn L",
                    pickerAddress = "44 Trường Sa, TP.HCM",
                    deliveryName = "Nguyễn Thị M",
                    deliveryAddress = "100 Pasteur, TP.HCM",
                    distance = "6.4 km",
                    estimatedTime = "30 phút",
                    income = 100000L,
                    hasCOD = true,
                    notion = "Ưu tiên giao nhanh",
                    fragileGoods = false,
                    isBusinessHours = false,
                    orderType = OrderType.ELECTRONICS,
                    orderState = OrderState.DELIVERING,
                    goodsWeight = "4.5 kg",
                    orderTime = "02/11/2025 18:00"
                )
            )
        }
    }

}
