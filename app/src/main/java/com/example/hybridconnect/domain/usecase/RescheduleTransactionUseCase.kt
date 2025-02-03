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
    private val dialUssdUseCase: DialUssdUseCase,
    ) {
    suspend operator fun invoke(originalTransaction: Transaction, newOffer: Offer, time: Long) {
        try {
            val scheduledTransaction = originalTransaction.copy(
                status = TransactionStatus.RESCHEDULED,
                offer = newOffer,
                rescheduleInfo = RescheduleInfo(time = time)
            )

            deleteTransactionUseCase(originalTransaction.id)
            createTransactionUseCase(scheduledTransaction)
            dialUssdUseCase(scheduledTransaction, time)
            updateTransactionStatusUseCase(
                scheduledTransaction.id,
                TransactionStatus.RESCHEDULED
            )
        } catch (e: Exception) {
            Log.d(TAG, e.message, e)
            throw e
        }
    }
}