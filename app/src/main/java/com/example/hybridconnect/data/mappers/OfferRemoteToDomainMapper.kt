package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.domain.enums.OfferType
import com.example.hybridconnect.data.remote.api.response.OfferRemote
import com.example.hybridconnect.domain.enums.OfferTag
import com.example.hybridconnect.domain.model.Offer
import java.util.UUID

fun OfferRemote.toDomain(): Offer {
    return Offer(
        id = UUID.fromString(this.id),
        name = this.name,
        ussdCode = this.ussdCode,
        price = this.price,
        type = OfferType.valueOf(this.type),
        tag = this.tag?.takeIf { it.isNotEmpty() }?.let { OfferTag.valueOf(it) },
        isSiteLinked = true
    )
}