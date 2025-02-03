package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.AutoReplyEntity
import com.example.hybridconnect.domain.enums.AutoReplyType
import com.example.hybridconnect.domain.model.AutoReply

fun AutoReplyEntity.toDomain(): AutoReply {
    return AutoReply(
        title = title,
        type = AutoReplyType.valueOf(type),
        message = message,
        isActive = isActive
    )
}