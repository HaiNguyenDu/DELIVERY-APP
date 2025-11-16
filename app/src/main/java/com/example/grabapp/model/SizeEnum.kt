package com.example.grabapp.model

enum class SizeEnum(val value: String) {
    S("S"), M("M"), L("L"), XL("XL")
}

fun getSizeEnum(sizeString: String): SizeEnum {
    return when (sizeString) {
        SizeEnum.S.value -> SizeEnum.S
        SizeEnum.M.value -> SizeEnum.M
        SizeEnum.L.value -> SizeEnum.L
        SizeEnum.XL.value -> SizeEnum.XL
        else -> SizeEnum.S
    }
}