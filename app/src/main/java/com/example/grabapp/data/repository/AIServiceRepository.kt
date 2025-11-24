package com.example.grabapp.data.repository

import com.example.grabapp.api.RetrofitProvider
import com.example.grabapp.common.BASE_URL
import com.example.grabapp.data.ai.AIServiceApi
import com.example.grabapp.data.model.UploadFaceRequest
import retrofit2.Response

class AIServiceRepository {
    private val api: AIServiceApi = RetrofitProvider
        .create(BASE_URL)
        .create(AIServiceApi::class.java)

    suspend fun uploadFace(
        uuid: String,
        request: UploadFaceRequest
    ): Response<String> {
        return api.uploadFace(uuid, request)
    }
}
