package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.TransactionRepository
import javax.inject.Inject

private const val TAG = "GetLastTransactionForCustomerUseCase"

class GetLastTransactionForCustomerUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(customer: Customer): Transaction? {
        try {
            return transactionRepository.getLastTransactionForCustomer(customer)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            throw e
        }
    }
}