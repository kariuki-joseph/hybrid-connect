package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.SubscriptionPackageEntity
import com.example.hybridconnect.domain.model.SubscriptionPackage

fun SubscriptionPackage.toEntity(): SubscriptionPackageEntity {
    return SubscriptionPackageEntity(
        id = this.id,
        name = this.name,
        price = this.price,
        description = this.description,
        limit = this.limit,
        type = this.type,
        updatedAt = this.updatedAt,
    )
}