package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ObserveTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())

    suspend operator fun invoke(): StateFlow<List<Transaction>> {
        transactionRepository.getTransactions()
        return _transactions.asStateFlow()
    }
}