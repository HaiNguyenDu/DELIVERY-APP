package com.example.grabapp.domain.enum

enum class PackageTypeEnum(val value: String) {
    Clothes("Quần áo"),
    Electronic("Điện tử"),
    Fragile("Dễ vỡ"),
    Other("Khác");

    companion object {
        fun getPackageTypeByString(text: String): PackageTypeEnum {
            return when (text) {
                Clothes.value -> Clothes
                Electronic.value -> Electronic
                Fragile.value -> Fragile
                else -> Other
            }
        }
    }
}