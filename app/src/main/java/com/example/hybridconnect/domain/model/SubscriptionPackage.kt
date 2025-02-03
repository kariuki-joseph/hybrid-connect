package com.example.hybridconnect.domain.model

import com.example.hybridconnect.domain.enums.SubscriptionType
import java.util.UUID

data class SubscriptionPackage(
    val id: UUID,
    val name: String,
    val price: Int,
    val description: String,
    val limit: Double,
    val type: SubscriptionType,
    val updatedAt: Long? = null
)