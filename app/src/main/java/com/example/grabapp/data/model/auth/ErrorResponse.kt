package com.example.grabapp.data.model.auth

data class ErrorResponse(
    val error: String,
    val message: String,
    val status: Int
)