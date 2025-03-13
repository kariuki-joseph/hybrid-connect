package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.model.Transaction
import javax.inject.Inject

private const val TAG = "RetryTransactionUseCase"

class RetryTransactionUseCase @Inject constructor(
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val decrementCustomerBalanceUseCase: DecrementCustomerBalanceUseCase,
    private val forwardMessagesUseCase: ForwardMessagesUseCase,
) {
    suspend operator fun invoke(transaction: Transaction) {
        try {

        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }
}