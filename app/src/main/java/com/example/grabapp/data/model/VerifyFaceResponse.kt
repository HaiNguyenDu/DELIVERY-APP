package com.example.grabapp.data.model

data class VerifyFaceResponse(
    val success: Boolean,
    val data: VerifyFaceData?
)

data class VerifyFaceData(
    val similarity: Double?,
    val match: Boolean?,
    val images_processed: Int?,
    val total_images: Int?,
    val errors: String?
)
