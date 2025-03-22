package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.enums.SmsType
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.SmsMessage
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.utils.getSmsType
import javax.inject.Inject

private const val TAG = "CreateTransactionUseCase"

class CreateTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(sms: SmsMessage, offer: Offer?): Transaction {
        try {
            val existingMpesaTransaction =
                transactionRepository.getTransactionByMpesaCode(sms.mpesaCode)
            if (existingMpesaTransaction != null) {
                throw Exception("Cannot process transaction. Existing transaction with same M-Pesa code exists")
            }

            val smsType = getSmsType(sms.message)
            when (smsType) {
                SmsType.AIRTIME_BALANCE -> throw Exception("Cannot record airtime balance transactions")
                SmsType.RECOMMENDATION_TIMEOUT -> throw Exception("Cannot record recommendation timeout transactions")
                else -> {}
            }

            val transaction = Transaction(
                id = 0,
                mpesaCode = sms.mpesaCode,
                amount = offer?.price ?: sms.amount,
                mpesaMessage = sms.message,
                status = TransactionStatus.PENDING,
                offer = offer,
            )

            val transactionId = transactionRepository.createTransaction(transaction)
            return transaction.copy(id = transactionId)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }
}
