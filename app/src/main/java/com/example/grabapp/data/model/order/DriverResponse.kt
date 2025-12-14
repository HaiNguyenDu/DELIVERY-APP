package com.example.grabapp.data.model.order

import java.time.LocalDate
import java.time.LocalDateTime

data class DriverResponse(
    val id: String,
    val userId: String,
    val vehiclePlateNumber: String?,
    val avatarUrl: String?,
    val licenseNumber: String?,
    val identityFullName: String,
    val identityNumber: String,
    val identityIssueDate: String?,
    val identityIssuePlace: String?,
    val identityAddress: String?,
    val identityGender: Gender,
    val identityBirthdate: String?,
    val status: DriverStatus,
    val ratingAvg: Double?,
    val ratingCount: Int?,
    val approvedAt: String?,
    val createdAt: String?,
    val updatedAt: String?
)

enum class Gender {
    MALE, FEMALE, OTHER
}

enum class DriverStatus {
    PENDING,
    APPROVED,
    REJECTED,
    SUSPENDED,
    INACTIVE
}
