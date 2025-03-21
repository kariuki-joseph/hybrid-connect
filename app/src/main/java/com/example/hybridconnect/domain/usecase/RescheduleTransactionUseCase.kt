package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.Transaction
import javax.inject.Inject

private const val TAG = "RescheduleTransactionUseCase"

class RescheduleTransactionUseCase @Inject constructor(
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val forwardTransactionUseCase: ForwardTransactionUseCase,
    ) {
    suspend operator fun invoke(originalTransaction: Transaction, newOffer: Offer, time: Long) {
        try {
        } catch (e: Exception) {
            Log.d(TAG, e.message, e)
            throw e
        }
    }
}