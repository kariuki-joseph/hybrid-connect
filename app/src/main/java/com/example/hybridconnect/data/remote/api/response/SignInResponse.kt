package com.example.hybridconnect.data.remote.api.response

import com.example.hybridconnect.domain.enums.SiteLinkAccountType
import com.google.gson.annotations.SerializedName

data class SignInResponse(
    val token: String,
    val agent: ApiAgent,
    val siteLink: ApiSiteLink?
)

data class ApiAgent(
    @SerializedName("_id")
    val userId: String,
    val name: String,
    val phone: String,
    val email: String,
    val pin: String,
    val isActive: Boolean,
    val isVerified: Boolean,
)

data class ApiSiteLink (
    val siteLinkId: String,
    val siteLinkUrl: String,
    val siteName: String,
    val accountType: SiteLinkAccountType,
    val accountNumber: String,
    val isActive: Boolean
)