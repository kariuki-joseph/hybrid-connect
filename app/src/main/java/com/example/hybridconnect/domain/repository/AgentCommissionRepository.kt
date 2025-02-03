package com.example.hybridconnect.domain.repository

import com.example.hybridconnect.domain.model.AgentCommission
import com.example.hybridconnect.domain.model.Offer
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

interface AgentCommissionRepository {
    val agentCommissions: StateFlow<List<AgentCommission>>
    suspend fun addCommission(commission: AgentCommission)
    suspend fun fetchCommissionForDates(dates: List<String>): List<AgentCommission>
    suspend fun getCommissions(): List<AgentCommission>
    fun getCommissionForOffer(offer: Offer): Double
}