package com.example.hybridconnect.domain.model

data class MpesaMessage(
    override val mpesaCode: String,
    override val senderName: String,
    override val senderPhone: String,
    override val amount: Int,
    override val message: String,
    override val time: Long,
): SmsMessage