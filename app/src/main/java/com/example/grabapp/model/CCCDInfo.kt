package com.example.grabapp.model

data class CCCDInfo(
    val cccdNumber: String,
    val oldCmndNumber: String,
    val fullName: String,
    val dateOfBirth: String,
    val gender: String,
    val address: String,
    val issueDate: String
) {
    companion object {
        fun parseFromQRString(qrString: String): CCCDInfo? {
            try {
                val parts = qrString.split("|")
                if (parts.size < 7) return null

                return CCCDInfo(
                    cccdNumber = parts[0].trim(),
                    oldCmndNumber = parts[1].trim(),
                    fullName = parts[2].trim(),
                    dateOfBirth = parts[3].trim(),
                    gender = parts[4].trim(),
                    address = parts[5].trim(),
                    issueDate = parts[6].trim()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
}
