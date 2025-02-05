package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.RescheduleInfo
import com.example.hybridconnect.domain.model.Transaction
import javax.inject.Inject

private const val TAG = "RescheduleTransactionUseCase"

class RescheduleTransactionUseCase @Inject constructor(
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val updateTransactionStatusUseCase: UpdateTransactionStatusUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val forwardMessagesUseCase: ForwardMessagesUseCase,
    ) {
    suspend operator fun invoke(originalTransaction: Transaction, newOffer: Offer, time: Long) {
        try {
        } catch (e: Exception) {
            Log.d(TAG, e.message, e)
            throw e
        }
    }
}