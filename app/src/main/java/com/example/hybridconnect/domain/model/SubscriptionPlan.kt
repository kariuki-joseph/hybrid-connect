package com.example.hybridconnect.domain.model

import com.example.hybridconnect.domain.enums.SubscriptionType

data class SubscriptionPlan(
    val type: SubscriptionType,
    val limit: Long,
)

fun SubscriptionPlan.isExpired(): Boolean {
    return when(type) {
        SubscriptionType.LIMITED -> limit <= 0
        SubscriptionType.UNLIMITED -> limit <= System.currentTimeMillis()
    }
}