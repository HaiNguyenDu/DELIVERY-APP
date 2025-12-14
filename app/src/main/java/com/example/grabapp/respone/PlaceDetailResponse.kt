package com.example.grabapp.respone

import com.example.grabapp.domain.model.location.Address
import com.example.grabapp.data.model.order.AddressInfo
import com.example.grabapp.utils.Districts
import com.example.grabapp.utils.Wards
import com.google.gson.annotations.SerializedName

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
        val compound = this.compound
        val district = compound?.district ?: ""
        val ward = compound?.ward?: ""
        val districtCode = Districts.getCode(district) ?: -1
        val wardCode = Wards.getCode(ward) ?: -1
        return AddressInfo(
            latitude = this.geometry.location.lat,
            longitude = this.geometry.location.lng,
            detail = this.formatted_address,
            districtCode = districtCode,
            wardCode = wardCode,
        )
    }
}

