package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.local.dao.TransactionDao
import com.example.hybridconnect.data.local.entity.TransactionEntity
import com.example.hybridconnect.data.mappers.toDomain
import com.example.hybridconnect.data.mappers.toEntity
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.CustomerRepository
import com.example.hybridconnect.domain.repository.OfferRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import java.util.concurrent.PriorityBlockingQueue

private const val TAG = "TransactionRepositoryImpl"

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val customerRepository: CustomerRepository,
    private val offerRepository: OfferRepository,
) : TransactionRepository {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    override val transactionQueue = PriorityBlockingQueue<Transaction>()

    override val transactions: StateFlow<List<Transaction>> = _transactions
        .map { it.sortedByDescending { transaction -> transaction.createdAt } }
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    override suspend fun createTransaction(transaction: Transaction) {
        transactionDao.insert(transaction.toEntity())
        _transactions.value += transaction
    }

    override suspend fun createScheduledTransaction(
        originalTransaction: Transaction,
        scheduledTransaction: Transaction,
    ) {

    }

    override suspend fun getTransactions() {
        val transactions = transactionDao.getTransactions()
        _transactions.value = transactions.map {
            val customer = customerRepository.getCustomerById(it.customerId)
            val offer = it.offerId?.let { it1 -> offerRepository.getOfferById(it1) }
            it.toDomain(customer, offer)
        }
    }

    override suspend fun getTransactionsForCustomer(customer: Customer): List<Transaction> {
        return transactionDao.getTransactionsByCustomerId(customer.id).map {
            val offer = it.offerId?.let { offerId -> offerRepository.getOfferById(offerId) }
            it.toDomain(customer, offer)
        }
    }

    override suspend fun deleteTransactionsForCustomer(customer: Customer) {
        try {
            transactionDao.deleteTransactionsForCustomer(customer.id)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }

    override suspend fun getTransactionsBetween(startTime: Long, endTime: Long): List<Transaction> {
        return transactionDao.getTransactionsBetween(startTime, endTime).map {
            val customer = customerRepository.getCustomerById(it.customerId)
            val offer = it.offerId?.let { offerId -> offerRepository.getOfferById(offerId) }
            it.toDomain(customer, offer)
        }
    }

    override suspend fun getScheduledTransactionsForTransactionId(transactionId: UUID): List<Transaction> {
        val transactions = transactionDao.getScheduledTransactionsForTransactionId(transactionId)

        return transactions.map { transaction ->
            val customer = customerRepository.getCustomerById(transaction.customerId)
            val offer = transaction.offerId?.let { offerRepository.getOfferById(it) }
            transaction.toDomain(customer, offer)
        }
    }

    override suspend fun getTransactionById(transactionId: UUID): Transaction? {
        val transaction = transactionDao.getTransactionById(transactionId) ?: return null

        val customer = customerRepository.getCustomerById(transaction.customerId)
        val offer = transaction.offerId?.let { offerRepository.getOfferById(it) }
        return transaction.toDomain(customer, offer)
    }

    override suspend fun updateTransactionStatus(transactionId: UUID, status: TransactionStatus) {
        _transactions.value = _transactions.value.map { transaction ->
            if (transaction.id == transactionId) {
                transaction.copy(status = status)
            } else {
                transaction
            }
        }

        val transaction: TransactionEntity? = transactionDao.getTransactionById(transactionId)
        transaction?.let {
            it.status = status
            transactionDao.update(it)
        }
    }

    override suspend fun updateTransactionResponse(transactionId: UUID, responseMessage: String) {
        val transaction = transactionDao.getTransactionById(transactionId)
        transaction?.let {
            it.responseMessage = responseMessage
            transactionDao.update(it)
            _transactions.value = _transactions.value.map { transaction ->
                if (transaction.id == transactionId) {
                    transaction.copy(responseMessage = responseMessage)
                } else {
                    transaction
                }
            }
        }
    }

    override suspend fun deleteTransaction(id: UUID) {
        try {
            transactionDao.deleteTransaction(id)
            _transactions.value = _transactions.value.filter { it.id != id }
        } catch (e: Exception) {
            Log.e(TAG, "deleteTransaction", e)
            throw e
        }
    }

    override suspend fun getLastTransactionForCustomer(customer: Customer): Transaction? {
        try {
            val transaction = transactionDao.getLastTransactionForCustomer(customer.id)
            val offer = transaction?.offerId?.let { offerRepository.getOfferById(it) }
            return transaction?.toDomain(customer, offer)
        } catch (e: Exception) {
            Log.e(TAG, "getLastTransactionForCustomer", e)
            throw e
        }
    }

    override suspend fun incrementRetriesForTransaction(transactionId: UUID) {
        transactionDao.incrementRetries(transactionId)
        val transaction = transactionDao.getTransactionById(transactionId)
        transaction?.let {
            _transactions.value = _transactions.value.map { currentTransaction ->
                if (currentTransaction.id == transactionId) {
                    currentTransaction.copy(retries = it.retries)
                } else {
                    currentTransaction
                }
            }
        }
    }

    override suspend fun resetRetriesForTransaction(transactionId: UUID) {
        transactionDao.resetRetries(transactionId)
    }
}