package com.example.hybridconnect.domain.model

import com.example.hybridconnect.domain.enums.AutoReplyType

data class AutoReply(
    val title: String,
    val type: AutoReplyType,
    val message: String,
    val isActive: Boolean = false
)