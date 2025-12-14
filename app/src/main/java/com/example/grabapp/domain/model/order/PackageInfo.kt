package com.example.grabapp.domain.model.order

import com.example.grabapp.data.model.order.AddressInfo
import com.example.grabapp.domain.enum.PackageTypeEnum
import com.example.grabapp.domain.enum.PayerTypeEnum
import com.example.grabapp.domain.enum.SizeEnum

data class PackageItemModel(
    val id: String = "",
    val weightKg: Double = 0.0,
    val packageSize: SizeEnum? = null,
    val codAmount: Double = 0.0,
    val cod: Boolean = false,
    val payerType: PayerTypeEnum = PayerTypeEnum.SENDER,
    val category: PackageTypeEnum = PackageTypeEnum.KHAC,
    val description: String = "",
    val dropOffAddress: AddressInfo = AddressInfo(),
    val imgUrl: String = ""
) {
    override fun toString(): String {
        return "${packageSize?.name}-$weightKg kg-${category.value}"
    }

    fun isHashInfo(): Boolean {
        return (weightKg >= 0.0 && dropOffAddress.name.isNotEmpty() && dropOffAddress.phone.isNotEmpty())
    }

    fun canCalculatePrice(): Boolean {
        return weightKg >= 0.0 && packageSize != null && dropOffAddress.detail.isNotEmpty()
    }
}
