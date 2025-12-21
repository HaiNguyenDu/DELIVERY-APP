package com.example.grabapp.model

data class DeliveryAddressItem(
    val name: String,
    val address: String,
    val isPickup: Boolean,
    val packageId: String? = null,
    val packageStatus: PackageStatus? = null,
    val phone: String? = null
)