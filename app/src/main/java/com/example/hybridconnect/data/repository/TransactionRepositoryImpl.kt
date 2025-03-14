package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.local.dao.TransactionDao
import com.example.hybridconnect.data.mappers.toDomain
import com.example.hybridconnect.data.mappers.toEntity
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.OfferRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.PriorityBlockingQueue
import javax.inject.Inject

private const val TAG = "TransactionRepositoryImpl"

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val offerRepository: OfferRepository,
) : TransactionRepository {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    override val transactionQueue = PriorityBlockingQueue<Transaction>()

    override suspend fun createTransaction(transaction: Transaction): Long {
        try {
            val transactionId = transactionDao.insert(transaction.toEntity())
            _transactions.value += transaction.copy(id = transactionId)
            return transactionId
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            throw e
        }
    }

    override suspend fun getTransactions(): StateFlow<List<Transaction>> {
        try {
            val transactions = transactionDao.getTransactions()
            _transactions.value = transactions.map { transactionEntity ->
                val offer = transactionEntity.offerId?.let { offerRepository.getOfferById(it) }
                transactionEntity.toDomain(offer)
            }
            return _transactions.asStateFlow()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            throw e
        }
    }

    override val transactionQueueFlow: Flow<List<Transaction>>
        get() = transactionDao.getUnForwardedTransactionsFlow().map { transactionEntities ->
            transactionEntities.map { entity ->
                val offer = entity.offerId?.let { offerRepository.getOfferById(it) }
                entity.toDomain(offer)
            }
        }

    override suspend fun deleteTransaction(transaction: Transaction) {
        try {
            transactionDao.deleteTransaction(transaction.id)
            _transactions.value = _transactions.value.filter { it.id != transaction.id }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }

    override suspend fun getTransactionByMpesaCode(mpesaCode: String): Transaction? {
        val transaction = transactionDao.getTransactionByMpesaCode(mpesaCode) ?: return null
        val offer = transaction.offerId?.let { offerRepository.getOfferById(it) }
        return transaction.toDomain(offer)
    }

    override suspend fun updateTransaction(updatedTransaction: Transaction) {
        try {
            transactionDao.updateTransaction(updatedTransaction.toEntity())
            _transactions.value = _transactions.value.map { transaction ->
                if (transaction.id == updatedTransaction.id) updatedTransaction else transaction
            }
        } catch (e: Exception) {
            Log.e(TAG, "updateTransaction", e)
            throw e
        }
    }
}