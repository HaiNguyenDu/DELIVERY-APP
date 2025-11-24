package com.example.grabapp.data.ai

import com.example.grabapp.data.model.UploadFaceRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Path

interface AIServiceApi {
    suspend fun uploadFace(
        @Path("uuid") uuid: String,
        @Body body: UploadFaceRequest
    ): Response<String>
}
