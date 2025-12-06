package com.example.grabapp.data.model.order

data class PriceRouteItem(
    val price: Double,
    val latitude: Double,
    val longitude: Double,
    val routeIndex: Int,
    val packageIndex: Int,
    val distance: Int,
    val estimatedDuration: Int
)