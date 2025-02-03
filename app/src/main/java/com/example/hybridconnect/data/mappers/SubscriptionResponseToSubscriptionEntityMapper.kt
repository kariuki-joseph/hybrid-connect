package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.SubscriptionPackageEntity
import com.example.hybridconnect.data.remote.api.response.SubscriptionResponse
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

fun SubscriptionResponse.toEntity(): SubscriptionPackageEntity {
    return SubscriptionPackageEntity(
        id = UUID.fromString(this.id),
        name = this.name,
        price = this.amount,
        description = this.description,
        limit = this.limit,
        updatedAt = convertToEpochMillis(this.updatedAt),
        type = this.type
    )
}

private fun convertToEpochMillis(isoDate: String): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date = dateFormat.parse(isoDate)
    return date?.time ?: 0L
}