package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.model.Agent
import com.example.hybridconnect.domain.repository.AuthRepository
import javax.inject.Inject

private const val TAG = "UpdateAgentUseCase"

class UpdateAgentUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(agent: Agent){
        try {
            authRepository.updateAgentRemote(agent)
            authRepository.saveAgent(agent)
        } catch (e: Exception){
            Log.e(TAG, e.message.toString())
            e.printStackTrace()
            throw e
        }
    }
}