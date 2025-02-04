package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.ConnectedAppEntity
import com.example.hybridconnect.domain.model.ConnectedApp

fun ConnectedApp.toEntity(): ConnectedAppEntity {
    return ConnectedAppEntity(
        connectId = this.connectId,
        appName = this.appName,
        isOnline = this.isOnline,
        messagesSent = this.messagesSent
    )
}