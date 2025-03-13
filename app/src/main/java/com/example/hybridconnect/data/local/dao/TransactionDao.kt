package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.hybridconnect.data.local.entity.TransactionEntity

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE mpesaCode = :mpesaCode LIMIT 1")
    suspend fun getTransactionByMpesaCode(mpesaCode: String): TransactionEntity?

    @Query("SELECT * FROM transactions ORDER BY createdAt ASC LIMIT 1")
    suspend fun getOldestTransaction(): TransactionEntity?

    @Query("SELECT * FROM transactions ORDER BY createdAt ASC")
    suspend fun getTransactions(): List<TransactionEntity>

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getCurrentTransactionSize(): Int

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Int)

}