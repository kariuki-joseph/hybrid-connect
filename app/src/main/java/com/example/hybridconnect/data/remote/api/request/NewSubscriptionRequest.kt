package com.example.hybridconnect.data.remote.api.request

import com.google.gson.annotations.SerializedName

data class NewSubscriptionRequest(
    val phone: String,
    @SerializedName("package")
    val packageId: String
)