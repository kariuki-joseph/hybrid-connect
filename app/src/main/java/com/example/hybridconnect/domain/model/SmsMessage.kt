package com.example.hybridconnect.domain.model

interface SmsMessage {
    val mpesaCode: String
    val senderName: String
    val senderPhone: String
    val amount: Int
    val message: String
    val time: Long
}