package com.example.hybridconnect.domain.model

import java.util.UUID

data class Agent(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String,
    val pin: String,
)