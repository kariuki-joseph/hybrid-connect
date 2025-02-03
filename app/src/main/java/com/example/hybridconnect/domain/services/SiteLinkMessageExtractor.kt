package com.example.hybridconnect.domain.services

import com.example.hybridconnect.domain.model.SiteLinkMessage
import com.example.hybridconnect.domain.model.SmsMessage
import com.example.hybridconnect.domain.services.interfaces.MessageExtractor

class SiteLinkMessageExtractor : MessageExtractor {
// Sample Message: BHSL 0748481418:1:Payment Confirmed

    override fun extractDetails(message: String): SmsMessage {
        val parts = message.split(":")
        val senderWithPrefix = parts.getOrNull(0)?.trim() ?: "Unknown"
        val sender = senderWithPrefix.removePrefix("BHSL").trim()
        val amount = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 0
        return SiteLinkMessage(
            senderName = sender,
            senderPhone = sender,
            amount = amount,
            message = message,
            time = System.currentTimeMillis()
        )
    }
}
