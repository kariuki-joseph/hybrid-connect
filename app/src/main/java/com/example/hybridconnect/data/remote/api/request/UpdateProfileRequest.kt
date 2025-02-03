package com.example.hybridconnect.data.remote.api.request

data class UpdateProfileRequest(
    val name: String?,
    val email: String?,
    val phone: String?,
    val pin: String?
)
