package com.example.grabapp.driver.register.data

import android.graphics.Bitmap

data class DrivingLicenseData(
    var frontCardBitmap: Bitmap? = null,
    var backCardBitmap: Bitmap? = null,
    var idNumber: String = "",
    var category: String? = null,
    var issueDate: Long? = null
) {
    fun isValidIdNumber(): Boolean {
        return idNumber.matches(Regex("^[A-Z0-9]{1,12}$"))
    }

    fun hasAllRequiredFields(): Boolean {
        return frontCardBitmap != null &&
                backCardBitmap != null &&
                idNumber.isNotEmpty() &&
                isValidIdNumber() &&
                category != null &&
                issueDate != null
    }

    fun validateIssueDate(): Boolean {
        if (issueDate == null) return false
        val currentTime = System.currentTimeMillis()
        return issueDate!! < currentTime
    }

    fun isFullyValid(): Boolean {
        return hasAllRequiredFields() && validateIssueDate()
    }

    fun getMissingFields(): List<String> {
        val missingFields = mutableListOf<String>()
        if (frontCardBitmap == null) {
            missingFields.add("Ảnh mặt trước")
        }
        if (backCardBitmap == null) {
            missingFields.add("Ảnh mặt sau")
        }
        if (idNumber.isEmpty() || !isValidIdNumber()) {
            missingFields.add("Số bằng lái xe")
        }
        if (category == null) {
            missingFields.add("Hạng bằng lái")
        }
        if (issueDate == null) {
            missingFields.add("Ngày cấp")
        } else if (!validateIssueDate()) {
            missingFields.add("Ngày cấp phải trước ngày hiện tại")
        }
        return missingFields
    }
}

