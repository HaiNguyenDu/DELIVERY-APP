package com.example.grabapp.extention

import java.text.DecimalFormat

fun Double.toMoneyFormat(): String {
    val formatter = DecimalFormat("#,###.##")
    return formatter.format(this).replace(',', '.')
}