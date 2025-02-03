package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hybridconnect.data.local.entity.AgentEntity
import java.util.UUID

@Dao
interface AgentDao {
    @Insert
    suspend fun createAgent(agent: AgentEntity)

    @Query("SELECT * FROM agents WHERE id = :agentId")
    suspend fun getAgent(agentId: UUID): AgentEntity?

    @Update
    suspend fun updateAgent(agent: AgentEntity)

    @Query("UPDATE agents SET pin = :pin WHERE id = :agentId")
    suspend fun updateAgentPin(agentId: UUID, pin: String)

    @Delete
    suspend fun deleteAgent(agent: AgentEntity)
}