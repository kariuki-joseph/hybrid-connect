package com.example.hybridconnect.data.remote.api.response

import com.example.hybridconnect.domain.enums.SiteLinkAccountType

data class SiteLinkResponse(
    val siteLinkId: String,
    val siteLinkUrl: String,
    val siteName: String,
    val accountType: SiteLinkAccountType,
    val accountNumber: String,
)