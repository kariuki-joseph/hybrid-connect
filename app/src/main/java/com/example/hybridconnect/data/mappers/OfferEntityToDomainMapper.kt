package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.OfferEntity
import com.example.hybridconnect.domain.model.Offer

fun OfferEntity.toDomain(): Offer {
    return Offer(
        id = this.id,
        name = this.name,
        ussdCode = this.ussdCode,
        price = this.price,
        type = this.type,
        tag = this.tag,
        isSiteLinked = this.isSiteLinked
    )
}