package com.example.grabapp.respone

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coordinates(
    val lat: Double,
    val lng: Double
) : Parcelable {
    override fun toString(): String {
        return "$lat,$lng"
    }
}