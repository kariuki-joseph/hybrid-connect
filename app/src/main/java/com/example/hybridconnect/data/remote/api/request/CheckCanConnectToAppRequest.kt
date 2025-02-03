package com.example.hybridconnect.data.remote.api.request

data class CheckCanConnectToAppRequest(
    val agentId: String,
    val connectId: String
)