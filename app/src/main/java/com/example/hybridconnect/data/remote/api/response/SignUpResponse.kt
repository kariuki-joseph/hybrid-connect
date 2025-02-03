package com.example.hybridconnect.data.remote.api.response

import com.google.gson.annotations.SerializedName

data class SignUpResponse(

    val userId: String,
    val token: String,
)
