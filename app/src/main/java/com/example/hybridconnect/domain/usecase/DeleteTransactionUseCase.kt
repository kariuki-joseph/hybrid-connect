package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.repository.TransactionRepository
import java.util.UUID
import javax.inject.Inject

private const val TAG = "DeleteTransactionUseCase"

class DeleteTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val decrementCustomerBalanceUseCase: DecrementCustomerBalanceUseCase,
) {
    suspend operator fun invoke(transactionId: UUID) {
        try {

        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }
}