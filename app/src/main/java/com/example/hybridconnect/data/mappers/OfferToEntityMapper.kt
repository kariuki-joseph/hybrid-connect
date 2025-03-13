package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.OfferEntity
import com.example.hybridconnect.domain.model.Offer


fun Offer.toEntity(): OfferEntity {
    return OfferEntity(
        id = this.id,
        name = this.name,
        price = this.price,
        ussdCode = this.ussdCode,
        type = this.type,
        tag = this.tag,
        isSiteLinked = this.isSiteLinked
    )
}