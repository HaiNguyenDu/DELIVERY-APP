package com.example.grabapp.data.repository

import android.util.Log
import com.example.grabapp.data.api.FileApi
import com.example.grabapp.data.model.file.UpLoadRequestMultiple
import com.example.grabapp.data.model.file.UploadResponse
import com.example.grabapp.data.model.file.UploadResponseMultiple
import com.example.grabapp.domain.repository.FileRepository
import com.example.grabapp.network.ApiProvider
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class FileRepositoryImpl : FileRepository {
    val api = ApiProvider.getInstance().getNoAuthApi(FileApi::class.java)
    override suspend fun upload(
        uid: String,
        file: File
    ): UploadResponse? {
        try {
            val uidBody =
                uid.toRequestBody("text/plain".toMediaType())
            val requestFile = file.asRequestBody("image/jpeg".toMediaType())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val response = api.uploadFile(uidBody, body)
            if (response.isSuccessful && response.body() != null) {
                return response.body()!!
            }
        } catch (e: Exception) {
            Log.e("FileRepository", "upload: ${e.message}")
        }
        return null
    }

    override suspend fun uploadMultiple(
        uid: String,
        listData: List<String>
    ): UploadResponseMultiple? {
        try {
            val request = UpLoadRequestMultiple(uid = uid, file = listData)
            val response = api.upLoadMultiple(request)
            if (response.isSuccessful && response.body() != null) {
                return response.body()!!
            }
        } catch (e: Exception) {
            Log.e("FileRepository", "upload: ${e.message}")
        }
        return null
    }
}