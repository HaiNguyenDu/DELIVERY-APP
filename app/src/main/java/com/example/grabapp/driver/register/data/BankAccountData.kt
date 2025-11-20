package com.example.grabapp.driver.register.data

data class BankAccountData(
    var accountName: String = "",
    var accountNumber: String = "",
    var bankName: String? = null,
    var isConfirmed: Boolean = false
) {
    fun isValidAccountName(): Boolean {
        return accountName.matches(Regex("^[a-zA-ZÀ-ỹ\\s]+$"))
    }

    fun isValidAccountNumber(): Boolean {
        return accountNumber.matches(Regex("^\\d{10,}$"))
    }

    fun hasAllRequiredFields(): Boolean {
        return accountName.isNotEmpty() &&
                isValidAccountName() &&
                accountNumber.isNotEmpty() &&
                isValidAccountNumber() &&
                bankName != null &&
                isConfirmed
    }

    fun isFullyValid(): Boolean {
        return hasAllRequiredFields()
    }

    fun getMissingFields(): List<String> {
        val missingFields = mutableListOf<String>()
        if (accountName.isEmpty()) {
            missingFields.add("Tên tài khoản")
        } else if (!isValidAccountName()) {
            missingFields.add("Tên tài khoản chỉ được chứa chữ cái")
        }
        if (accountNumber.isEmpty()) {
            missingFields.add("Số tài khoản")
        } else if (!isValidAccountNumber()) {
            missingFields.add("Số tài khoản phải là số và nhiều hơn 9 chữ số")
        }
        if (bankName == null) {
            missingFields.add("Tên ngân hàng")
        }
        if (!isConfirmed) {
            missingFields.add("Xác nhận thông tin")
        }
        return missingFields
    }
}

