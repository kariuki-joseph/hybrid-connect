package com.example.hybridconnect.domain.repository

import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.SmsMessage
import com.example.hybridconnect.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.PriorityBlockingQueue

interface TransactionRepository {
    val transactionQueue: PriorityBlockingQueue<Transaction>

    val transactionQueueFlow: Flow<List<Transaction>>

    suspend fun createTransaction(transaction: Transaction): Long

    suspend fun getTransactions(): StateFlow<List<Transaction>>

    suspend fun deleteTransaction(transaction: Transaction)

    suspend fun getTransactionByMpesaCode(mpesaCode: String): Transaction?

    suspend fun updateTransaction(updatedTransaction: Transaction)
}
