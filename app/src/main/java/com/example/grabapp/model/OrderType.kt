package com.example.grabapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class OrderType(val displayName: String) : Parcelable {
    FOOD("Thực phẩm"),
    CLOTHING("Quần áo"),
    ELECTRONICS("Điện tử"),
    FRAGILE("Dễ vỡ"),
    OTHER("Khác")
}
