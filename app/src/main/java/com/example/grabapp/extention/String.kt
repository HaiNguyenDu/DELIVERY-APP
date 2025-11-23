package com.example.grabapp.extention

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.formatToVietNamTime(): String {
    return try {
        val paresedData = LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy")
        formatter.format(paresedData)
    } catch (e: Exception) {
        ""
    }
}