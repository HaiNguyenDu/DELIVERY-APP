package com.example.grabapp.utils

object Wards {

    val nameToCode = mapOf(
        "hòa hiệp bắc" to 101,
        "hòa hiệp nam" to 102,
        "hòa khánh bắc" to 103,
        "hòa khánh nam" to 104,
        "hòa minh" to 105,

        "thanh khê tây" to 201,
        "thanh khê đông" to 202,
        "xuân hà" to 203,
        "chính gián" to 204,
        "thạc gián" to 205,
        "an khê" to 206,

        "thanh bình" to 301,
        "thuận phước" to 302,
        "thạch thang" to 303,
        "hải châu" to 304,
        "phước ninh" to 305,
        "hòa thuận tây" to 306,
        "bình thuận" to 307,
        "hòa cường bắc" to 308,
        "hòa cường nam" to 309,

        "thọ quang" to 401,
        "nại hiên đông" to 402,
        "mân thái" to 403,
        "an hải bắc" to 404,
        "phước mỹ" to 405,
        "an hải nam" to 406,

        "mỹ an" to 501,
        "khuê mỹ" to 502,
        "hòa quý" to 503,
        "hòa hải" to 504,

        "khuê trung" to 601,
        "hòa phát" to 602,
        "hòa an" to 603,
        "hòa thọ tây" to 604,
        "hòa thọ đông" to 605,
        "hòa xuân" to 606,

        "xã hòa bắc" to 701,
        "xã hòa liên" to 702,
        "xã hòa ninh" to 703,
        "xã hòa sơn" to 704,
        "xã hòa nhơn" to 705,
        "xã hòa phú" to 706,
        "xã hòa phong" to 707,
        "xã hòa châu" to 708,
        "xã hòa tiến" to 709,
        "xã hòa phước" to 710,
        "xã hòa khương" to 711
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
