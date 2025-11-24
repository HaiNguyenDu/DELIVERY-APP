package com.example.grabapp.domain.model.order

import com.example.grabapp.data.model.order.CreateOrderRequest
import com.example.grabapp.data.model.order.PackageItem
import com.example.grabapp.domain.enum.PackageTypeEnum
import com.example.grabapp.domain.enum.PayerTypeEnum
import com.example.grabapp.domain.enum.SizeEnum

fun PackageItemModel.toDto(): PackageItem {
    return PackageItem(
        id = this.id,
        weightKg = this.weightKg,
        packageSize = this.packageSize?.name ?: "",
        deliveryFee = this.deliveryFee,
        codFee = this.codFee,
        payerType = this.payerType.name,
        category = this.category.name,
        description = this.description,
        dropoffAddress = this.dropOffAddress
    )
}

fun PackageItem.toDomain(): PackageItemModel {
    return PackageItemModel(
        id = this.id,
        weightKg = this.weightKg,
        packageSize = try {
            SizeEnum.valueOf(this.packageSize)
        } catch (e: Exception) {
            null
        },
        deliveryFee = this.deliveryFee,
        codFee = this.codFee,
        payerType = try {
            PayerTypeEnum.valueOf(this.payerType)
        } catch (e: Exception) {
            PayerTypeEnum.SENDER
        },
        category = try {
            PackageTypeEnum.valueOf(this.category)
        } catch (e: Exception) {
            PackageTypeEnum.KHAC
        },
        description = this.description,
        dropOffAddress = this.dropoffAddress
    )
}

fun OrderForm.toCreateOrderRequest(): CreateOrderRequest {
    val pickup = this.pickupAddress
        ?: throw IllegalArgumentException("Pickup address must not be null")
    val packageList = this.listPackageInfo.map { it.toDto() }

    return CreateOrderRequest(
        pickupAddress = pickup,
        packages = packageList,
        customerNote = this.customerNote.takeIf { it.isNotEmpty() },
        fragile = this.fragile
    )
}
