package com.example.grabapp.respone

import com.example.grabapp.domain.model.location.Address

data class GeocodeResponse(
    val results: List<GeocodeResult>,
    val status: String
)

data class GeocodeResult(
    val address_components: List<AddressComponent>,
    val formatted_address: String,
    val geometry: Geometry,
    val place_id: String,
    val reference: String,
    val plus_code: PlusCode?,
    val compound: Compound?,
    val types: List<String>,
    val name: String?,
    val address: String?
){
    fun toAddress(): Address {
        return Address(
            this.geometry.location,
            this.name ?: "",
            this.address ?: ""
        )
    }
}

data class AddressComponent(
    val long_name: String,
    val short_name: String
)

data class Geometry(
    val location: Coordinates,
    val boundary: Any?
)
