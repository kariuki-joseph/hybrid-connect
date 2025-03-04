package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.local.dao.TransactionDao
import com.example.hybridconnect.data.mappers.toDomain
import com.example.hybridconnect.data.mappers.toEntity
import com.example.hybridconnect.domain.model.SmsMessage
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import java.util.concurrent.PriorityBlockingQueue
import javax.inject.Inject

private const val TAG = "TransactionRepositoryImpl"

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
) : TransactionRepository {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    override val transactionQueue = PriorityBlockingQueue<Transaction>()

    private val _queueSize = MutableStateFlow(0)
    override val queueSize: StateFlow<Int> = _queueSize.asStateFlow()

    init {
        updateTransactionSize()
    }

    override suspend fun createTransaction(transaction: Transaction) {
        try {
            transactionDao.insert(transaction.toEntity())
            _transactions.value += transaction
            updateTransactionSize()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            throw e
        }
    }

    override suspend fun getTransactions(): StateFlow<List<Transaction>> {
        try {
            val transactions = transactionDao.getTransactions()
            _transactions.value = transactions.map { it.toDomain() }
            return _transactions.asStateFlow()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            throw e
        }
    }

    override suspend fun getOldestTransaction(): Transaction? {
        try {
            val transaction = transactionDao.getOldestTransaction()
            return transaction?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            throw e
        }
    }

    override suspend fun deleteTransaction(id: Int) {
        try {
            transactionDao.deleteTransaction(id)
            _transactions.value = _transactions.value.filter { it.id != id }
            updateTransactionSize()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }

    override fun createFromMessage(message: String): Transaction {
        return Transaction(
            id = 0,
            message = message,
            createdAt = System.currentTimeMillis()
        )
    }

    private fun updateTransactionSize(){
        CoroutineScope(Dispatchers.IO).launch {
            val size = transactionDao.getCurrentTransactionSize()
            _queueSize.value = size
        }

    }

}