package com.example.grabapp.domain.model.location

import com.example.grabapp.respone.Coordinates
import com.google.gson.annotations.SerializedName
import org.maplibre.android.geometry.LatLng

class Address(
    val detail: String = "",
    val name: String = "",
    val phone: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    fun toLatLng(): LatLng = LatLng(latitude, longitude)
}