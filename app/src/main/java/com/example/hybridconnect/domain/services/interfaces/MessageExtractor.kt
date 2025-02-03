package com.example.hybridconnect.domain.services.interfaces

import com.example.hybridconnect.domain.model.SmsMessage

interface MessageExtractor {
    fun extractDetails(message: String): SmsMessage
}