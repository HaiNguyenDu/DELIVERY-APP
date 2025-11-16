package com.example.grabapp.respone



data class AutoCompleteResponse(
    val predictions: List<Prediction>,
    val execution_time: String?,
    val status: String?
)