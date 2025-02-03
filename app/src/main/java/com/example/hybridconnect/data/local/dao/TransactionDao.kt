package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hybridconnect.data.local.entity.TransactionEntity
import java.util.UUID

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: UUID): TransactionEntity?

    @Query("SELECT * FROM transactions ORDER BY time ASC")
    suspend fun getTransactions(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE customerId = :customerId")
    suspend fun getTransactionsByCustomerId(customerId: Int): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE time BETWEEN :startTime AND :endTime")
    suspend fun getTransactionsBetween(startTime: Long, endTime: Long): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE reschedule_parentTransactionId = :transactionId")
    suspend fun getScheduledTransactionsForTransactionId(transactionId: UUID): List<TransactionEntity>

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: UUID)

    @Query("DELETE FROM transactions WHERE customerId = :customerId")
    suspend fun deleteTransactionsForCustomer(customerId: Int)

    @Query("""
        SELECT * FROM transactions 
        WHERE customerId = :customerId 
        ORDER BY createdAt DESC 
        LIMIT 1
    """)
    suspend fun getLastTransactionForCustomer(customerId: Int): TransactionEntity?

    @Query("UPDATE transactions SET retries = retries + 1 WHERE id = :transactionId")
    suspend fun incrementRetries(transactionId: UUID)

    @Query("UPDATE transactions SET retries = 0 WHERE id = :transactionId")
    suspend fun resetRetries(transactionId: UUID)

}