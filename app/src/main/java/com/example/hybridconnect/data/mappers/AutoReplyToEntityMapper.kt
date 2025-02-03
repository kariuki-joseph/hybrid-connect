package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.AutoReplyEntity
import com.example.hybridconnect.domain.model.AutoReply

fun AutoReply.toEntity(): AutoReplyEntity {
    return AutoReplyEntity(
        title = title,
        type = type.name,
        message = message,
        isActive = isActive
    )
}