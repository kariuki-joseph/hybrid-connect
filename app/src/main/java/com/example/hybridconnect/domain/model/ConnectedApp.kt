package com.example.hybridconnect.domain.model

data class ConnectedApp(
    val connectId: String,
    val isOnline: Boolean,
    val messagesSent: Int = 0,
)