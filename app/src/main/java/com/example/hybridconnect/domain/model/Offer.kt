package com.example.hybridconnect.domain.model

import com.example.hybridconnect.domain.enums.OfferTag
import com.example.hybridconnect.domain.enums.OfferType
import java.util.UUID

data class Offer(
    val id: UUID,
    val name: String,
    val ussdCode: String,
    val price: Int,
    val type: OfferType,
    val tag: OfferTag? = null,
    val isSiteLinked: Boolean = false,
)
