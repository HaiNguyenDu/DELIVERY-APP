package com.example.grabapp.domain.repository

import com.example.grabapp.data.model.file.UploadResponse
import com.example.grabapp.data.model.file.UploadResponseMultiple
import java.io.File

interface FileRepository {
    suspend fun upload(uid: String, file: File): UploadResponse?
    suspend fun uploadMultiple(uid: String, listData: List<String>): UploadResponseMultiple?
}