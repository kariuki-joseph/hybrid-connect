package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.hybridconnect.data.local.entity.AgentCommissionEntity

@Dao
interface AgentCommissionDao {
    @Insert
    suspend fun insertCommission(commission: AgentCommissionEntity)

    @Query("UPDATE agent_commissions SET amount = amount + :amount WHERE date = :date")
    suspend fun incrementCommissionForDate(date: String, amount: Double)

    @Query("SELECT * FROM agent_commissions")
    suspend fun getAllCommissions(): List<AgentCommissionEntity>

    @Query("SELECT * FROM agent_commissions WHERE date = :date")
    suspend fun getCommissionForDate(date: String): AgentCommissionEntity?
}