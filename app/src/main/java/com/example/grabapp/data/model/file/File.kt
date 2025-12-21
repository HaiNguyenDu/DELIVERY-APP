package com.example.grabapp.data.model.file

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("message") val message: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("url") val url: String,
    @SerializedName("public_id") val publicId: String,
    @SerializedName("preview_url") val previewUrl: String?
)


data class FileDownloadResponse(
    @SerializedName("message") val message: String,
    @SerializedName("url") val url: String,
    @SerializedName("preview_url") val previewUrl: String?,
    @SerializedName("public_id") val publicId: String,
    @SerializedName("resource_type") val resourceType: String,
    @SerializedName("format") val format: String,
    @SerializedName("created_at") val createdAt: String
)

data class UpLoadRequest(
    @SerializedName("uid") val uid: String
)

data class UpLoadRequestMultiple(
    @SerializedName("file") val file: List<String>,
    @SerializedName("uid") val uid: String
)

data class UploadResponseMultiple(
    val message: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("total_files")
    val totalFile: Int,
    @SerializedName("successful_uploads")
    val isSuccess: Int,
    @SerializedName("failed_uploads")
    val isFail: Int,
    val files: List<UploadedFile>,
    val errors: List<String>
)

data class UploadedFile(
    val url: String,
    @SerializedName("public_id")
    val publicId: String,
    @SerializedName("preview_url")
    val previewUrl: String,
    val filename: String
)
