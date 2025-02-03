package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.AgentCommission
import com.example.hybridconnect.domain.repository.AgentCommissionRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetCommissionForDatesUseCase @Inject constructor(
    private val agentCommissionRepository: AgentCommissionRepository,
) {
    val agentCommissions: StateFlow<List<AgentCommission>> =
        agentCommissionRepository.agentCommissions

    suspend operator fun invoke(dates: List<String>): StateFlow<List<AgentCommission>> {
        agentCommissionRepository.fetchCommissionForDates(dates)
        return agentCommissions
    }
}