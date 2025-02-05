package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.model.Transaction
import javax.inject.Inject

private const val TAG = "RetryTransactionUseCase"

class RetryTransactionUseCase @Inject constructor(
    private val updateTransactionStatusUseCase: UpdateTransactionStatusUseCase,
    private val decrementCustomerBalanceUseCase: DecrementCustomerBalanceUseCase,
    private val forwardMessagesUseCase: ForwardMessagesUseCase,
) {
    suspend operator fun invoke(transaction: Transaction) {
        try {
            if (transaction.offer == null) {
                throw Exception("Offer is empty. Could not retry transaction")
            }
            updateTransactionStatusUseCase(transaction.id, TransactionStatus.SCHEDULED)
            decrementCustomerBalanceUseCase(transaction.customer, transaction.offer.price)
            forwardMessagesUseCase(transaction)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }
}