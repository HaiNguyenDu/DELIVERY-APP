package com.example.grabapp.respone

import com.example.grabapp.domain.model.location.Address
import com.example.grabapp.data.model.order.AddressInfo

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
    fun toAddressInfo(): AddressInfo {
        return AddressInfo(
            latitude = this.geometry.location.lat,
            longitude = this.geometry.location.lng,
            detail = this.formatted_address,
        )
    }
}