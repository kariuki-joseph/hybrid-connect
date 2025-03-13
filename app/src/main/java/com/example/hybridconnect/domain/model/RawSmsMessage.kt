package com.example.hybridconnect.domain.model

data class RawSmsMessage(
    val message: String,
    val sender: String,
    val simSlot: Int?,
    val timestamp: Long
)
