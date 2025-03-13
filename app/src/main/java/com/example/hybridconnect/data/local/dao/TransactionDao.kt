package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hybridconnect.data.local.entity.TransactionEntity

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE mpesaCode = :mpesaCode LIMIT 1")
    suspend fun getTransactionByMpesaCode(mpesaCode: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE isForwarded=0 ORDER BY createdAt ASC LIMIT 1")
    suspend fun getOldestTransaction(): TransactionEntity?

    @Query("SELECT * FROM transactions ORDER BY createdAt ASC")
    suspend fun getTransactions(): List<TransactionEntity>

    @Query("SELECT COUNT(*) FROM transactions WHERE isForwarded=0")
    suspend fun getQueuedTransactionsCount(): Int

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

}