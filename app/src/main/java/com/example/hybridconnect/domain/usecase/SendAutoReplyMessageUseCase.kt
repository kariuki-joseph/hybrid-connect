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
        if(transaction.type == TransactionType.SUBSCRIPTION_RENEWAL){
            return
        }

        val autoReply = autoReplyRepository.getAutoReplyByType(autoReplyType)
        if (autoReply.isActive) {
            val offer = transaction.offer
            val customer = transaction.customer
            val customerName = customer.name
            val firstName = customerName.split(" ")[0]
            val surname = customerName.split(" ")[customerName.split(" ").size - 1]
            val message = autoReply.message
                .replace("<firstName>", firstName)
                .replace("<surname>", surname)
                .replace("<amount>", transaction.amount.toString())
                .replace("<offerName>", offer?.name ?: "null")
                .replace("<offerPrice>", surname)

            Log.d(TAG, "Sending message $message to $${customer.phone}")
            sendSmsUseCase(customer.phone, message)
        }
    }
}