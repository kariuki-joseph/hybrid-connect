package com.example.hybridconnect.data.remote.api.response

import com.google.gson.annotations.SerializedName

data class AdminPhoneResponse(
    @SerializedName("_id")
    val id: String,
    val phone: String,
    val useCase: String,
    val isActive: Boolean = false
)
