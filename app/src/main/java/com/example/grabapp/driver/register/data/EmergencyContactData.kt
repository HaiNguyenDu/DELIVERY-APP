package com.example.grabapp.driver.register.data

data class EmergencyContactData(
    var name: String = "",
    var relationship: String? = null,
    var phone: String = "",
    var houseAddress: String = "",
    var commune: String = "",
    var province: String? = null
) {
    fun isValidPhone(): Boolean {
        return phone.length <= 10
    }

    fun hasAllRequiredFields(): Boolean {
        return name.isNotEmpty() &&
                relationship != null &&
                phone.isNotEmpty() &&
                isValidPhone() &&
                houseAddress.isNotEmpty() &&
                commune.isNotEmpty() &&
                province != null
    }

    fun isFullyValid(): Boolean {
        return hasAllRequiredFields()
    }

    fun getMissingFields(): List<String> {
        val missingFields = mutableListOf<String>()
        if (name.isEmpty()) {
            missingFields.add("Tên người liên hệ")
        }
        if (relationship == null) {
            missingFields.add("Mối quan hệ")
        }
        if (phone.isEmpty()) {
            missingFields.add("Số điện thoại")
        } else if (!isValidPhone()) {
            missingFields.add("Số điện thoại không được quá 10 ký tự")
        }
        if (houseAddress.isEmpty()) {
            missingFields.add("Địa chỉ")
        }
        if (commune.isEmpty()) {
            missingFields.add("Xã/Phường")
        }
        if (province == null) {
            missingFields.add("Tỉnh/Thành phố")
        }
        return missingFields
    }
}

