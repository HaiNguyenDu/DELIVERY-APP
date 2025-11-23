package com.example.grabapp.domain.model.location

import com.example.grabapp.respone.Coordinates
import org.maplibre.android.geometry.LatLng

class Address(
    val coordinates: Coordinates = Coordinates(0.0, 0.0),
    val name: String = "",
    val address: String = "",
) {
    fun getFormattedAddress(): String {
        return when {
            name.isEmpty() && address.isEmpty() -> ""
            name.isEmpty() -> address
            address.isEmpty() -> name
            else -> "$name, $address"
        }
    }

    fun toLatLng(): LatLng = LatLng(coordinates.lat, coordinates.lng)
}