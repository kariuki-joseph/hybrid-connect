package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.SubscriptionPlanEntity
import com.example.hybridconnect.domain.enums.SubscriptionType
import com.example.hybridconnect.domain.model.SubscriptionPlan

fun SubscriptionPlanEntity.toDomain(): SubscriptionPlan {
    return SubscriptionPlan(
        type = SubscriptionType.valueOf(this.type),
        limit = this.limit
    )
}