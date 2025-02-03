package com.example.hybridconnect.domain.repository

import com.example.hybridconnect.domain.enums.AutoReplyType
import com.example.hybridconnect.domain.model.AutoReply

interface AutoReplyRepository {
    suspend fun getAutoReplies(): List<AutoReply>
    suspend fun getAutoReplyByType(autoReplyType: AutoReplyType): AutoReply
    suspend fun updateAutoReply(autoReply: AutoReply)
    suspend fun activateAutoReply(autoReplyType: AutoReplyType)
    suspend fun deactivateAutoReply(autoReplyType: AutoReplyType)
}