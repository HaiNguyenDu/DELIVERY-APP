package com.example.grabapp.data.model

import com.google.gson.Gson

data class DriverProfile(
    val name: String,
    val cccdNumber: String,
    val licenseNumber: String,
    val birthDate: Long,
    val gender: String,
    val issueDate: Long,
    val issuePlace: String,
    val address: String,
    val vehicleType: String,
    val vehiclePlate: String,
    val phone: String? = null
) {
    companion object {
        fun fromJson(json: String): DriverProfile? {
            return try {
                Gson().fromJson(json, DriverProfile::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun toJson(profile: DriverProfile): String {
            return try {
                Gson().toJson(profile)
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }
}
