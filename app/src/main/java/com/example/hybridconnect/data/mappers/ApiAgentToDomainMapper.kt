package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.remote.api.response.ApiAgent
import com.example.hybridconnect.domain.model.Agent
import java.util.UUID

fun ApiAgent.toDomain(): Agent {
    return Agent(
        id = UUID.fromString(this.userId),
        firstName = getFirstName(this.name),
        lastName = getLastName(this.name),
        phoneNumber = this.phone,
        email = this.email,
        pin = this.pin
    )
}

private fun getFirstName(name: String): String {
    return name.split(" ")[0]
}

private fun getLastName(name: String): String {
    if (!name.contains(" ")) return ""
    return name.split(" ")[1]
}