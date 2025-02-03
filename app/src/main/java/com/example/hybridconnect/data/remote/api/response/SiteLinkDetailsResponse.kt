package com.example.hybridconnect.data.remote.api.response

import com.google.gson.annotations.SerializedName

data class SiteLinkDetailsResponse(
    val siteDetails: SiteDetails,
    val offers: List<OfferRemote>
)

data class SiteDetails(
    val siteName: String,
    val isActive: Boolean
)

data class OfferRemote(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val ussdCode: String,
    val price: Int,
    val type: String,
    val tag: String?,
    val createdAt: String,
    val updatedAt: String
)