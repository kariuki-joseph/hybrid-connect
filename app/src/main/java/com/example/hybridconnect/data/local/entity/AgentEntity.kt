package com.example.hybridconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "agents")
data class AgentEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String,
    val pin: String,
)