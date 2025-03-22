package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.hybridconnect.data.local.entity.TransactionEntity
import com.example.hybridconnect.data.local.entity.TransactionWithDetailsEntity
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.model.TransactionStatusCount
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: String): TransactionEntity?

    @Transaction
    @Query("SELECT * FROM transactions WHERE mpesaCode = :mpesaCode LIMIT 1")
    suspend fun getTransactionByMpesaCode(mpesaCode: String): TransactionWithDetailsEntity?

    @Transaction
    @Query("SELECT * FROM transactions WHERE status = :status ORDER BY createdAt ASC")
    fun getUnForwardedTransactionsFlow(status: TransactionStatus = TransactionStatus.PENDING): Flow<List<TransactionWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY createdAt ASC")
    fun getTransactions(): Flow<List<TransactionWithDetailsEntity>>

    @Query("SELECT COUNT(*) FROM transactions WHERE isForwarded=0")
    suspend fun getQueuedTransactionsCount(): Int

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    @Transaction
    @Query("""
    SELECT t.appId as appId, t.status, COUNT(*) as count
    FROM transactions t
    GROUP BY t.appId, t.status
    """)
    fun getTransactionStatusCounts(): Flow<List<TransactionStatusCount>>
}