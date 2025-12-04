package com.example.grabapp.data.model

data class UpdateDriverStatusRequest(
    val isAvailable: Boolean,
    val fcmToken: String
)
