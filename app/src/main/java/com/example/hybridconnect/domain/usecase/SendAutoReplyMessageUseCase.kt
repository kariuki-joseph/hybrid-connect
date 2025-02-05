package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.enums.AutoReplyType
import com.example.hybridconnect.domain.enums.TransactionType
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.AutoReplyRepository
import javax.inject.Inject

private const val TAG = "SendAutoReplyMessageUseCase"
class SendAutoReplyMessageUseCase @Inject constructor(
    private val autoReplyRepository: AutoReplyRepository,
    private val sendSmsUseCase: SendSmsUseCase,
) {
    suspend operator fun invoke(transaction: Transaction, autoReplyType: AutoReplyType) {

    }
}