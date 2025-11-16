package com.example.grabapp.respone

import com.example.grabapp.model.Address

class PlaceDetailResponse(
    val result: PlaceDetailResult,
    val status: String
)
class PlaceDetailResult(
    val formatted_address: String,
    val geometry: Geometry,
    val place_id: String,
    val plus_code: PlusCode?,
    val compound: Compound?,
    val types: List<String>,
    val url: String = "",
    val name:String = ""
){
    fun toAddress(): Address{
        return Address(
            this.geometry.location,
            name,
            formatted_address
        )
    }
}