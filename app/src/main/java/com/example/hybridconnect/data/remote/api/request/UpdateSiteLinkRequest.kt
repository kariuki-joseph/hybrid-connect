package com.example.hybridconnect.data.remote.api.request

data class UpdateSiteLinkRequest(
    val siteLinkId: String,
    val siteName: String,
    val accountType: String,
    val accountNumber: String
)