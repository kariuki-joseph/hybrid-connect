package com.example.hybridconnect.data.remote.api.response

import com.google.gson.annotations.SerializedName

data class CommissionRateResponse(
    @SerializedName("_id") val id: String,
    val amount: Int,
    val rate: String,
    val updatedAt: String
)
