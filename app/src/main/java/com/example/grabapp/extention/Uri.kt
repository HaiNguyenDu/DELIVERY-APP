package com.example.grabapp.extention

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max

fun Uri.toMultipartBodyPart(
    context: Context,
    partName: String = "file"
): MultipartBody.Part? {
    return try {
        val inputStream = context.contentResolver.openInputStream(this)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (originalBitmap == null) return null

        val maxSide = max(originalBitmap.width, originalBitmap.height)
        val targetMaxSide = 720

        val scale = targetMaxSide.toFloat() / maxSide

        val newWidth = (originalBitmap.width * scale).toInt()
        val newHeight = (originalBitmap.height * scale).toInt()

        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

        val file = File.createTempFile("face_verify_", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(file)

        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.flush()
        outputStream.close()

        if (resizedBitmap != originalBitmap) resizedBitmap.recycle()
        originalBitmap.recycle()

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        MultipartBody.Part.createFormData(partName, file.name, requestFile)

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
