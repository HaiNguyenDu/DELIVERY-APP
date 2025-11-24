package com.example.grabapp.data.ai

import com.example.grabapp.data.model.UploadFaceRequest
import com.example.grabapp.data.model.VerifyFaceResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface AIServiceApi {
    @POST("ai/face/upload/{uuid}")
    suspend fun uploadFace(
        @Path("uuid") uuid: String,
        @Body body: UploadFaceRequest
    ): Response<String>

    @Multipart
    @POST("ai/face/verify")
    suspend fun verifyFace(
        @Part("uid") uid: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<VerifyFaceResponse>
}
