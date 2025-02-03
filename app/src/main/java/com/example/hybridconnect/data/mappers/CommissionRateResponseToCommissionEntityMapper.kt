package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.CommissionRateEntity
import com.example.hybridconnect.data.remote.api.response.CommissionRateResponse
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

fun CommissionRateResponse.toEntity(): CommissionRateEntity {
    return CommissionRateEntity(
        id = UUID.fromString(this.id),
        amount = this.amount,
        rate = convertToDouble(this.rate),
        updatedAt = convertToEpochMillis(this.updatedAt)
    )
}

private fun convertToDouble(rate: String): Double {
    return (rate.removeSuffix("%").toInt() / 100).toDouble()
}

private fun convertToEpochMillis(isoDate: String): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date = dateFormat.parse(isoDate)
    return date?.time ?: 0L
}


