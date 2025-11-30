package com.example.grabapp.data.model

data class DriverRegisterResponse(
    val id: String,
    val userId: String,
    val vehiclePlateNumber: String,
    val licenseNumber: String,
    val identityFullName: String,
    val identityNumber: String,
    val identityIssueDate: String,
    val identityIssuePlace: String,
    val identityAddress: String,
    val identityGender: String,
    val identityBirthdate: String,
    val status: String,
    val ratingAvg: Double,
    val ratingCount: Int,
    val approvedAt: String?,
    val createdAt: String,
    val updatedAt: String
)
