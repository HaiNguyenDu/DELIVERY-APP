package com.example.grabapp.data.repository

import com.example.grabapp.api.RetrofitProvider
import com.example.grabapp.common.BASE_URL
import com.example.grabapp.data.file.FileApi
import com.example.grabapp.data.model.UploadFileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class FileRepository {
    private val api: FileApi = RetrofitProvider
        .create(BASE_URL)
        .create(FileApi::class.java)

    suspend fun uploadFile(
        uid: RequestBody,
        file: MultipartBody.Part
    ): Response<UploadFileResponse> {
        return api.uploadFile(uid, file)
    }
}
