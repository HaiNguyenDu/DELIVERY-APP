package com.example.grabapp.respone

data class Coordinates(
    val lat: Double,
    val lng: Double
){
    override fun toString(): String {
        return "$lat,$lng"
    }
}