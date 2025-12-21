package com.example.grabapp.data.api

import com.example.grabapp.data.model.file.UpLoadRequest
import com.example.grabapp.data.model.file.UpLoadRequestMultiple
import com.example.grabapp.data.model.file.UploadResponse
import com.example.grabapp.data.model.file.UploadResponseMultiple
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface FileApi {

    @Multipart
    @POST("file/upload")
    suspend fun uploadFile(
        @Part("uid") uid: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    @POST("file/upload-multiple")
    suspend fun upLoadMultiple(
        @Body upLoadRequestMultiple: UpLoadRequestMultiple
    ): Response<UploadResponseMultiple>

    @GET("file/download/{public_id}")
    suspend fun downLoadFile(
        @Path("public_id") publicId: String
    )
}