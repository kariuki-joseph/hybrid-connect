package com.example.hybridconnect.domain.services

import android.util.Log
import com.example.hybridconnect.domain.model.MpesaMessage
import com.example.hybridconnect.domain.model.SmsMessage
import com.example.hybridconnect.domain.services.interfaces.MessageExtractor
import java.text.SimpleDateFormat
import java.util.Locale

class TillMessageExtractor : MessageExtractor {
    override fun extractDetails(message: String): SmsMessage {
        val senderName = extractSenderName(message)
        val phoneNumber = extractPhoneNumber(message)
        val amount = extractAmount(message)
        val time = extractTime(message)
        return MpesaMessage(senderName, phoneNumber, amount, message, time = time)
    }

    private fun extractSenderName(message: String): String {
        val sender = extractWithRegex(message, "from \\d{12} (.+?)\\. New", 1) ?: "Unknown"
        return sender
    }

    private fun extractPhoneNumber(message: String): String {
        return extractWithRegex(message, "(\\d{12})", 0)?.takeLast(9)?.let { "0$it" } ?: "Unknown"
    }

    private fun extractAmount(message: String): Int {
        // Adjust the regex to match numbers with optional commas and two decimal places
        val regex = "Ksh([\\d,]+\\.\\d{2})"
        val extractedAmount = extractWithRegex(message, regex, 1)

        return extractedAmount
            ?.replace(",", "")
            ?.toDouble()
            ?.toInt() ?: 0
    }

    private fun extractTime(message: String): Long {
        val time = extractWithRegex(message, "on (\\d{1,2}/\\d{1,2}/\\d{2,4} at \\d{1,2}:\\d{2} [AP]M)", 1)
        return time?.let { timeString ->
            val formatter = SimpleDateFormat("dd/MM/yy 'at' hh:mm a", Locale.ENGLISH)
            formatter.parse(timeString)?.time ?: 0
        } ?: 0
    }

    private fun extractWithRegex(message: String, pattern: String, group: Int): String? {
        val regex = pattern.toRegex()
        return regex.find(message)?.groups?.get(group)?.value
    }

}