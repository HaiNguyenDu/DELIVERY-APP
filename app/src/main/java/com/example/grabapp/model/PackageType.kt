package com.example.grabapp.model

enum class PackageType(val value: String) {
    Clothes("Quần áo"),
    Electronic("Điện tử"),
    Fragile("Dễ vỡ"),
    Other("Khác");

    companion object {
        fun getPackageTypeByString(text: String): PackageType {
            return when (text) {
                Clothes.value -> Clothes
                Electronic.value -> Electronic
                Fragile.value -> Fragile
                else -> Other
            }
        }
    }
}
