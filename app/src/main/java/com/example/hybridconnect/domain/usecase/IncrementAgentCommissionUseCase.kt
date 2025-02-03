package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.model.AgentCommission
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.repository.AgentCommissionRepository
import javax.inject.Inject

private const val TAG = "IncrementAgentCommissionUseCase"

class IncrementAgentCommissionUseCase @Inject constructor(
    private val agentCommissionRepository: AgentCommissionRepository,
) {
    suspend operator fun invoke(offer: Offer, date: String) {
        try {
            val commission = agentCommissionRepository.getCommissionForOffer(offer)
            agentCommissionRepository.addCommission(AgentCommission(date, commission))
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }
}