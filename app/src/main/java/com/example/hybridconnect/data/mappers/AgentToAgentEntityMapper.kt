package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.AgentEntity
import com.example.hybridconnect.domain.model.Agent

fun Agent.toEntity(): AgentEntity {
    return AgentEntity(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        phoneNumber = this.phoneNumber,
        pin = this.pin
    )
}