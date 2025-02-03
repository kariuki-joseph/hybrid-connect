package com.example.hybridconnect.data.remote.api.request

data class SignUpRequest(
    val name: String,
    val email: String,
    val phone: String,
    val pin: String,
)