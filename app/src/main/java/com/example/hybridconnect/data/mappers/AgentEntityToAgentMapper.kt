package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.AgentEntity
import com.example.hybridconnect.domain.model.Agent

fun AgentEntity.toDomain(): Agent {
    return Agent(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        email = this.email,
        pin = this.pin
    )
}