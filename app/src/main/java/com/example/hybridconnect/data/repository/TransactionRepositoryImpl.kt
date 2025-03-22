package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.local.dao.TransactionDao
import com.example.hybridconnect.data.mappers.toDomain
import com.example.hybridconnect.data.mappers.toEntity
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.model.TransactionStatusCount
import com.example.hybridconnect.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.PriorityBlockingQueue
import javax.inject.Inject

private const val TAG = "TransactionRepositoryImpl"

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
) : TransactionRepository {
    override val transactionQueue = PriorityBlockingQueue<Transaction>()

    override suspend fun createTransaction(transaction: Transaction): Long {
        try {
            val transactionId = transactionDao.insert(transaction.toEntity())
            return transactionId
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            throw e
        }
    }

    override fun getTransactions(): Flow<List<Transaction>> {
        try {
            return transactionDao.getTransactions().map { entities ->
                entities.map { it.toDomain() }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            throw e
        }
    }

    override val transactionQueueFlow: Flow<List<Transaction>>
        get() = transactionDao.getUnForwardedTransactionsFlow().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun deleteTransaction(transaction: Transaction) {
        try {
            transactionDao.deleteTransaction(transaction.id)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }

    override suspend fun getTransactionByMpesaCode(mpesaCode: String): Transaction? {
        val transaction = transactionDao.getTransactionByMpesaCode(mpesaCode) ?: return null
        return transaction.toDomain()
    }

    override suspend fun updateTransaction(updatedTransaction: Transaction) {
        try {
            transactionDao.updateTransaction(updatedTransaction.toEntity())
        } catch (e: Exception) {
            Log.e(TAG, "updateTransaction", e)
            throw e
        }
    }

    override fun getTransactionStatusCounts(): Flow<List<TransactionStatusCount>> {
        try {
            return transactionDao.getTransactionStatusCounts()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            throw e
        }}
}