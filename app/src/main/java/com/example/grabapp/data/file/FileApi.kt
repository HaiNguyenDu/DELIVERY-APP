package com.example.grabapp.data.file

import com.example.grabapp.data.model.UploadFileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileApi {
    @Multipart
    @POST("file/upload")
    suspend fun uploadFile(
        @Part("uid") uid: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<UploadFileResponse>
}
