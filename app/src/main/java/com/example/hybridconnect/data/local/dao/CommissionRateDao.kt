package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hybridconnect.data.local.entity.CommissionRateEntity
import java.util.UUID

@Dao
interface CommissionRateDao {
    @Insert
    suspend fun insertCommissionRate(rate: CommissionRateEntity)

    @Query("SELECT * FROM commission_rates WHERE id = :id")
    suspend fun getRateById(id: UUID): CommissionRateEntity?

    @Query("SELECT rate FROM commission_rates WHERE amount = :amount")
    suspend fun getRateForAmount(amount: Int): Double?

    @Query("SELECT * FROM commission_rates")
    suspend fun getAllRates(): List<CommissionRateEntity>

    @Update
    suspend fun updateCommissionRate(newRate: CommissionRateEntity)

    @Delete
    suspend fun deleteCommissionRate(rate: CommissionRateEntity)
}