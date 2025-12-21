package com.example.grabapp.extention

import android.content.Context
import android.net.Uri
import java.io.File

fun Uri.ToFile(context: Context, uri: Uri, fileName: String): File {
    val inputStream = context.contentResolver.openInputStream(uri)!!
    val file = File(context.cacheDir, fileName)
    file.createNewFile()
    file.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    return file
}