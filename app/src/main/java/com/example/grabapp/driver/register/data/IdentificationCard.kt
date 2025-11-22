package com.example.grabapp.driver.register.data

import android.graphics.Bitmap
import java.util.Calendar

data class IdentificationCard(
    var frontCardBitmap: Bitmap? = null,
    var backCardBitmap: Bitmap? = null,
    var issueDate: Long? = null,
    var expirationDate: Long? = null,
    var birthDate: Long? = null,
    var issuePlace: String? = null,
    var province: String? = null,
    var gender: String? = null,
    var idNumber: String = ""
) {
    fun isValidIdNumber(): Boolean {
        return idNumber.matches(Regex("^\\d{8,12}$"))
    }

    fun hasAllRequiredFields(): Boolean {
        return frontCardBitmap != null &&
                backCardBitmap != null &&
                idNumber.isNotEmpty() &&
                isValidIdNumber() &&
                issueDate != null &&
                expirationDate != null &&
                birthDate != null &&
                issuePlace != null &&
                province != null &&
                gender != null
    }

    private fun isAtLeast18YearsOld(birthDate: Long): Boolean {
        val birthCalendar = Calendar.getInstance().apply {
            timeInMillis = birthDate
        }
        val currentCalendar = Calendar.getInstance()
        val age = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
        val monthDiff = currentCalendar.get(Calendar.MONTH) - birthCalendar.get(Calendar.MONTH)
        val dayDiff =
            currentCalendar.get(Calendar.DAY_OF_MONTH) - birthCalendar.get(Calendar.DAY_OF_MONTH)

        return if (monthDiff < 0 || (monthDiff == 0 && dayDiff < 0)) {
            age - 1 >= 18
        } else {
            age >= 18
        }
    }

    fun validateBirthDate(): Boolean {
        if (birthDate == null) return false
        val currentTime = System.currentTimeMillis()
        return birthDate!! < currentTime && isAtLeast18YearsOld(birthDate!!)
    }

    fun validateIssueDate(): Boolean {
        if (issueDate == null) return false
        val currentTime = System.currentTimeMillis()
        if (issueDate!! >= currentTime) return false
        if (birthDate != null && issueDate!! <= birthDate!!) return false
        return true
    }

    fun validateExpirationDate(): Boolean {
        if (expirationDate == null) return false
        val currentTime = System.currentTimeMillis()
        if (expirationDate!! <= currentTime) return false
        if (issueDate != null && expirationDate!! <= issueDate!!) return false
        return true
    }

    fun validateDateLogic(): Boolean {
        return validateBirthDate() && validateIssueDate() && validateExpirationDate()
    }

    fun isFullyValid(): Boolean {
        return hasAllRequiredFields() && validateDateLogic()
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
            missingFields.add("Số CMND/CCCD")
        }
        if (birthDate == null) {
            missingFields.add("Ngày sinh")
        } else if (!validateBirthDate()) {
            missingFields.add("Ngày sinh phải trước ngày hiện tại và trên 18 tuổi")
        }
        if (issueDate == null) {
            missingFields.add("Ngày cấp")
        } else if (!validateIssueDate()) {
            if (issueDate!! >= System.currentTimeMillis()) {
                missingFields.add("Ngày cấp phải trước ngày hiện tại")
            } else if (birthDate != null && issueDate!! <= birthDate!!) {
                missingFields.add("Ngày cấp phải sau ngày sinh")
            }
        }
        if (issuePlace == null) {
            missingFields.add("Nơi cấp")
        }
        if (expirationDate == null) {
            missingFields.add("Ngày hết hạn")
        } else if (!validateExpirationDate()) {
            if (expirationDate!! <= System.currentTimeMillis()) {
                missingFields.add("Ngày hết hạn phải sau ngày hiện tại")
            } else if (issueDate != null && expirationDate!! <= issueDate!!) {
                missingFields.add("Ngày hết hạn phải sau ngày cấp")
            }
        }
        if (province == null) {
            missingFields.add("Tỉnh")
        }
        if (gender == null) {
            missingFields.add("Giới tính")
        }
        return missingFields
    }
}

