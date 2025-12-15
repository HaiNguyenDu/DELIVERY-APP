package com.example.grabapp.data.api

import com.example.grabapp.data.model.file.UpLoadRequest
import com.example.grabapp.data.model.file.UpLoadRequestMultiple
import com.example.grabapp.data.model.file.UploadResponse
import com.example.grabapp.data.model.file.UploadResponseMultiple
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FileApi {

    @POST("file/upload")
    suspend fun upLoad(
        @Body upLoadRequest: UpLoadRequest
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