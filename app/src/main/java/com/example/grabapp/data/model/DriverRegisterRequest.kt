package com.example.grabapp.data.model

data class DriverRegisterRequest(
    val vehiclePlateNumber: String,
    val licenseNumber: String,
    val identityFullName: String,
    val identityNumber: String,
    val identityIssueDate: String,
    val identityIssuePlace: String,
    val identityAddress: String,
    val identityGender: String,
    val identityBirthdate: String
)
