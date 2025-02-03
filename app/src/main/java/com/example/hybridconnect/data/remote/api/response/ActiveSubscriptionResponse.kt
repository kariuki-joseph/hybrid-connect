package com.example.hybridconnect.data.remote.api.response

import com.google.gson.annotations.SerializedName

data class ActiveSubscriptionResponse(
    @SerializedName("id")
    val subscriptionId: String?,
    val expiryTime: Long,
)
