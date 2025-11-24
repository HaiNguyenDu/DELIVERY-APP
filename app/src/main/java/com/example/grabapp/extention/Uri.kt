package com.example.grabapp.extention

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun Uri.toMultipartBodyPart(context: Context, partName: String = "file"): MultipartBody.Part? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(this)
        val file = File.createTempFile("upload_", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(file)
        
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        MultipartBody.Part.createFormData(partName, file.name, requestFile)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

