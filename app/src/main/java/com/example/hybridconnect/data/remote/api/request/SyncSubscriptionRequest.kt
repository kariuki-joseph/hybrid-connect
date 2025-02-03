package com.example.hybridconnect.data.remote.api.request

import com.example.hybridconnect.domain.enums.SubscriptionType

data class SyncSubscriptionRequest(
    val packageType: SubscriptionType,
    val limit: Long,
)