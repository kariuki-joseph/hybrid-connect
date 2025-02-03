package com.example.hybridconnect.domain.model

open class SiteLinkMessage(
    override val senderName: String,
    override val senderPhone: String,
    override val amount: Int,
    override val message: String,
    override val time: Long
) : SmsMessage
