package com.example.grabapp.domain.repository

import com.example.grabapp.data.model.file.UploadResponse
import com.example.grabapp.data.model.file.UploadResponseMultiple

interface FileRepository {
    suspend fun upload(uid: String, file: String): UploadResponse?
    suspend fun uploadMultiple(uid: String, listData: List<String>): UploadResponseMultiple?
}