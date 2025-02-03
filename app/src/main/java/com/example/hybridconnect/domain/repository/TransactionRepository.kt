package com.example.hybridconnect.domain.repository

import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.model.Transaction
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.PriorityBlockingQueue

interface TransactionRepository {

    val transactions: StateFlow<List<Transaction>>

    val transactionQueue: PriorityBlockingQueue<Transaction>

    suspend fun createTransaction(transaction: Transaction)

    suspend fun createScheduledTransaction(originalTransaction: Transaction, scheduledTransaction: Transaction)

    suspend fun getTransactions()

    suspend fun getTransactionsForCustomer(customer: Customer): List<Transaction>

    suspend fun deleteTransactionsForCustomer(customer: Customer)

    suspend fun getTransactionsBetween(startTime: Long, endTime: Long): List<Transaction>

    suspend fun getScheduledTransactionsForTransactionId(transactionId: UUID): List<Transaction>

    suspend fun getTransactionById(transactionId: UUID): Transaction?

    suspend fun updateTransactionStatus(transactionId: UUID, status: TransactionStatus)

    suspend fun updateTransactionResponse(transactionId: UUID, responseMessage: String)

    suspend fun deleteTransaction(id: UUID)

    suspend fun getLastTransactionForCustomer(customer: Customer): Transaction?

    suspend fun incrementRetriesForTransaction(transactionId: UUID)

    suspend fun resetRetriesForTransaction(transactionId: UUID)
}
