package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.ConnectedAppEntity
import com.example.hybridconnect.domain.model.ConnectedApp

fun ConnectedAppEntity.toDomain(): ConnectedApp {
    return ConnectedApp(
        connectId = this.connectId,
        isOnline = this.isOnline,
        messagesSent = this.messagesSent
    )
}