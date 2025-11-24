package com.example.grabapp.respone

import com.example.grabapp.data.model.order.AddressInfo

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
) {
    fun toAddressInfo(): AddressInfo {
        return AddressInfo(
            formatted_address,
            "",
            "",
            this.geometry.location.lat,
            this.geometry.location.lng
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
