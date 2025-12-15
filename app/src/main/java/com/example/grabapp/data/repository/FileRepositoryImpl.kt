package com.example.grabapp.data.repository

import android.util.Log
import com.example.grabapp.data.api.FileApi
import com.example.grabapp.data.model.file.UpLoadRequest
import com.example.grabapp.data.model.file.UpLoadRequestMultiple
import com.example.grabapp.data.model.file.UploadResponse
import com.example.grabapp.data.model.file.UploadResponseMultiple
import com.example.grabapp.domain.repository.FileRepository
import com.example.grabapp.network.ApiProvider

class FileRepositoryImpl : FileRepository {
    val api = ApiProvider.getInstance().getNoAuthApi(FileApi::class.java)
    override suspend fun upload(
        uid: String,
        file: String
    ): UploadResponse? {
        try {
            val request = UpLoadRequest(uid = uid, file = file)
            val response = api.upLoad(request)
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