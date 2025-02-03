package com.example.hybridconnect.domain.repository

import com.example.hybridconnect.domain.model.Agent
import com.example.hybridconnect.domain.model.AuthDetails
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val agent: StateFlow<Agent?>
    suspend fun registerAgent(agent: Agent)
    suspend fun loginUser(email: String, pin: String): AuthDetails
    suspend fun saveAgent(agent: Agent)
    suspend fun updateAgentRemote(agent: Agent)
    suspend fun updateAgentPin(pin: String)
    suspend fun sendOtp(email: String)
    suspend fun verifyOtp(otp: String): Agent
    suspend fun fetchAgent(): Agent
    suspend fun logoutUser(agent: Agent)
}