package com.example.grabapp.domain.model.order

import com.example.grabapp.data.model.order.AddressInfo
import com.example.grabapp.data.model.order.CreateOrderRequest
import com.example.grabapp.data.model.order.OrderItemResponse
import com.example.grabapp.data.model.order.PackageItem
import com.example.grabapp.data.model.order.PackageItemResponse
import com.example.grabapp.domain.enum.PackageTypeEnum
import com.example.grabapp.domain.enum.PayerTypeEnum
import com.example.grabapp.domain.enum.PaymentTypeEnum
import com.example.grabapp.domain.enum.SizeEnum

fun PackageItemModel.toDto(): PackageItem {
    return PackageItem(
        id = "",
        weightKg = this.weightKg,
        packageSize = this.packageSize?.name ?: "",
        codAmount = this.codAmount,
        cod = this.cod,
        payerType = this.payerType.name,
        category = this.category.name,
        description = this.description,
        imgUrl = this.imgUrl,
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
        codAmount = this.codAmount,
        cod = this.cod,
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
        imgUrl = "he",
        description = this.description,
        dropOffAddress = this.dropoffAddress
    )
}

fun OrderForm.toCreateOrderRequest(): CreateOrderRequest {
    val pickup = AddressInfo(
        detail = this.pickupAddress.detail,
        phone = "0914103372",
        name = "Nguyen duy hai",
        latitude = this.pickupAddress.latitude,
        longitude = this.pickupAddress.longitude
    )
        ?: throw IllegalArgumentException("Pickup address must not be null")
    val packageList = this.listPackageInfo.map { it.toDto() }

    return CreateOrderRequest(
        pickupAddress = pickup,
        packages = packageList,
        customerNote = this.customerNote.takeIf { it.isNotEmpty() },
        fragile = this.fragile,
        paymentMethod = paymentTypeEnum.name
    )
}

fun OrderItemResponse.toOrderForm(): OrderForm {
    return OrderForm(
        pickupAddress = pickupAddress,
        listPackageInfo = packages.map { it.toPackageItemModel() },
        customerNote = "",
        fragile = false,
        paymentTypeEnum = PaymentTypeEnum.CASH
    )
}

fun PackageItemResponse.toPackageItemModel(): PackageItemModel {
    return PackageItemModel(
        id = id,
        weightKg = weightKg,
        packageSize = SizeEnum.valueOf(packageSize),
        codAmount = codAmount,
        cod = cod,
        payerType = PayerTypeEnum.valueOf(payerType),
        category = PackageTypeEnum.valueOf(category),
        description = description,
        dropOffAddress = dropoffAddress,
        imgUrl = imgUrl
    )
}

