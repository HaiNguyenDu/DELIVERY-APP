package com.example.grabapp.data.ai

import com.example.grabapp.data.model.UploadFaceRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AIServiceApi {
    @POST("ai/face/upload/{uuid}")
    suspend fun uploadFace(
        @Path("uuid") uuid: String,
        @Body body: UploadFaceRequest
    ): Response<String>
}
