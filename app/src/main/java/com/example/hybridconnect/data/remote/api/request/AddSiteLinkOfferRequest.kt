package com.example.hybridconnect.data.remote.api.request

import com.example.hybridconnect.domain.enums.OfferTag
import com.example.hybridconnect.domain.enums.OfferType
import com.google.gson.annotations.SerializedName

data class AddSiteLinkOfferRequest (
    val offerId: String,
    val name: String,
    val ussdCode: String,
    val price: Int,
    val tag: OfferTag? = null,
    val type: OfferType
)