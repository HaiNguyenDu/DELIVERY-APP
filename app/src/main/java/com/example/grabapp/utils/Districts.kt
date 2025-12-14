package com.example.grabapp.utils

object Districts {

    val nameToCode = mapOf(
        "liên chiểu" to 1,
        "thanh khê" to 2,
        "hải châu" to 3,
        "sơn trà" to 4,
        "ngũ hành sơn" to 5,
        "cẩm lệ" to 6,
        "hòa vang" to 7
    )

    fun contains(name: String): Boolean {
        return nameToCode.containsKey(normalize(name))
    }

    fun getCode(name: String): Int? {
        return nameToCode[normalize(name)]
    }

    private fun normalize(text: String): String =
        text.trim().lowercase()
}
