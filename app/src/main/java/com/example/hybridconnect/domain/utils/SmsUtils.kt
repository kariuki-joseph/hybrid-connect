package com.example.hybridconnect.domain.utils

import com.example.hybridconnect.domain.enums.SmsType

fun getSmsType(message: String): SmsType {
    return when {
        // Check for SiteLink message by specific code pattern at the start
        message.startsWith("BHSL") -> SmsType.SITE_LINK

        // Check for Till message by regex pattern
        Regex("received from \\d{9,12}", RegexOption.IGNORE_CASE).containsMatchIn(message) -> SmsType.TILL

        // Check for Mpesa message by regex pattern
        Regex("received Ksh\\d+(\\.\\d{2})?", RegexOption.IGNORE_CASE).containsMatchIn(message) -> SmsType.MPESA

        // Check Recommendation Timeout messages by regex pattern
        Regex("Recommendation for (\\d{9,12}) timed out", RegexOption.IGNORE_CASE).containsMatchIn(message) -> SmsType.RECOMMENDATION_TIMEOUT
        // Default case if none match
        else -> throw IllegalArgumentException("Unknown SMS type")
    }
}

fun isAlreadyRecommendedResponse(responseMessage: String): Boolean {
    return responseMessage.contains("already been recommended", ignoreCase = true)
}

fun isInsufficientBalanceResponse(responseMessage: String): Boolean {
    return responseMessage.contains("insufficient account balance", ignoreCase = true)
}

fun isOutOfServiceResponse(responseMessage: String): Boolean {
    return responseMessage.contains("Service is currently unavailable", ignoreCase = true)
}