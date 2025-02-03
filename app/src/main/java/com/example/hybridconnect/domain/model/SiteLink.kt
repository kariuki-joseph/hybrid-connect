package com.example.hybridconnect.domain.model

import com.example.hybridconnect.domain.enums.SiteLinkAccountType

data class SiteLink(
    val id: String,
    val siteName: String,
    val url: String,
    val accountType: SiteLinkAccountType = SiteLinkAccountType.TILL,
    val accountNumber: String,
    val isActive: Boolean = false
)