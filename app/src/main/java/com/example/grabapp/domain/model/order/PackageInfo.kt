package com.example.grabapp.domain.model.order

import android.net.Uri
import com.example.grabapp.data.model.order.AddressInfo
import com.example.grabapp.domain.enum.PackageTypeEnum
import com.example.grabapp.domain.enum.PayerTypeEnum
import com.example.grabapp.domain.enum.SizeEnum

data class PackageItemModel(
    val id: String = "",
    val weightKg: Double = 0.0,
    val packageSize: SizeEnum? = null,
    val deliveryFee: Double = 0.0,
    val codAmount: Double = 0.0,
    val cod: Boolean = false,
    val payerType: PayerTypeEnum = PayerTypeEnum.SENDER,
    val category: PackageTypeEnum = PackageTypeEnum.KHAC,
    val description: String = "",
    val dropOffAddress: AddressInfo = AddressInfo(),
    val detailAddress: String = "",
    val imgUrl:String = ""
)
{
    override fun toString(): String {
        return "${packageSize?.name}-$weightKg kg-${category.value}"
    }

    fun isHashInfo(): Boolean{
        return (weightKg >= 0.0 && dropOffAddress.name.isNotEmpty() && dropOffAddress.detail.isNotEmpty() && dropOffAddress.phone.isNotEmpty())
    }
}
