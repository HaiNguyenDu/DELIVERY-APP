package com.example.grabapp.domain.model.order

import com.example.grabapp.data.model.order.AddressInfo

data class OrderForm(
    val pickupAddress: AddressInfo = AddressInfo(),
    val listPackageInfo: List<PackageItemModel> = mutableListOf(),
    val customerNote: String = "",
    val fragile: Boolean = false,
)