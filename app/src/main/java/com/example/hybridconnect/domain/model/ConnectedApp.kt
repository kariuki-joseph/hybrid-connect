package com.example.hybridconnect.domain.model

data class ConnectedApp(
    val connectId: String,
    val appName: String,
    val isOnline: Boolean = false,
    val messagesSent: Int = 0,
)