package com.example.hybridconnect.data.remote.api.request

import com.example.hybridconnect.domain.enums.SiteLinkAccountType

data class SiteLinkRequest(
    val siteName: String,
    val accountType: SiteLinkAccountType,
    val accountNumber: String
)