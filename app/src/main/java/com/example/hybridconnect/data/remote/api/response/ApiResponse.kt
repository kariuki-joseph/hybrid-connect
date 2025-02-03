package com.example.hybridconnect.data.remote.api.response

data class ApiResponse<T>(
    val success: Boolean,
    val msg: String?,
    val data: T?
)