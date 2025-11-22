package com.example.grabapp.driver.register.data

import android.graphics.Bitmap

data class VehicleRegistrationData(
    var frontCardBitmap: Bitmap? = null,
    var backCardBitmap: Bitmap? = null,
    var licensePlate: String = "",
    var fuelType: String? = null,
    var vehicleBrand: String? = null,
    var vehicleModel: String = "",
    var manufactureYear: Int? = null
) {
    fun isValidLicensePlate(): Boolean {
        return licensePlate.matches(Regex("^[A-Z0-9]+$"))
    }

    fun isValidManufactureYear(): Boolean {
        if (manufactureYear == null) return false
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        return manufactureYear!! < currentYear
    }

    fun hasAllRequiredFields(): Boolean {
        return frontCardBitmap != null &&
                backCardBitmap != null &&
                licensePlate.isNotEmpty() &&
                isValidLicensePlate() &&
                fuelType != null &&
                vehicleBrand != null &&
                vehicleModel.isNotEmpty() &&
                manufactureYear != null &&
                isValidManufactureYear()
    }

    fun isFullyValid(): Boolean {
        return hasAllRequiredFields()
    }

    fun getMissingFields(): List<String> {
        val missingFields = mutableListOf<String>()
        if (frontCardBitmap == null) {
            missingFields.add("Ảnh mặt trước")
        }
        if (backCardBitmap == null) {
            missingFields.add("Ảnh mặt sau")
        }
        if (licensePlate.isEmpty()) {
            missingFields.add("Biển số xe")
        } else if (!isValidLicensePlate()) {
            missingFields.add("Biển số xe chỉ được chứa chữ in hoa hoặc số")
        }
        if (fuelType == null) {
            missingFields.add("Loại nhiên liệu")
        }
        if (vehicleBrand == null) {
            missingFields.add("Hãng xe")
        }
        if (vehicleModel.isEmpty()) {
            missingFields.add("Mẫu xe")
        }
        if (manufactureYear == null) {
            missingFields.add("Năm sản xuất")
        } else if (!isValidManufactureYear()) {
            missingFields.add("Năm sản xuất phải trước năm hiện tại")
        }
        return missingFields
    }
}

