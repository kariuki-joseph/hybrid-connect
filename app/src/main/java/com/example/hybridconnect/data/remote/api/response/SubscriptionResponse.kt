package com.example.hybridconnect.data.remote.api.response

import com.example.hybridconnect.domain.enums.SubscriptionType
import com.google.gson.annotations.SerializedName

data class SubscriptionResponse(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val amount: Int,
    val description: String,
    val limit: Double,
    val type: SubscriptionType,
    val updatedAt: String
)