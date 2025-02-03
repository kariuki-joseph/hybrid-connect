package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.SubscriptionPackageEntity
import com.example.hybridconnect.domain.model.SubscriptionPackage

fun SubscriptionPackageEntity.toDomain(): SubscriptionPackage {
    return SubscriptionPackage(
        id = this.id,
        name = this.name,
        price = this.price,
        description = this.description,
        limit = this.limit,
        type = this.type
    )
}