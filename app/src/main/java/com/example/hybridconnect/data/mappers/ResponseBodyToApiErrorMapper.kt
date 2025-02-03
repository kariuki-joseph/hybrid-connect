package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.remote.api.response.ApiError
import com.google.gson.Gson
import okhttp3.ResponseBody

fun ResponseBody?.toApiError(): ApiError? {
    return try {
        Gson().fromJson(this?.string(), ApiError::class.java)
    } catch (e: Exception) {
        null
    }
}