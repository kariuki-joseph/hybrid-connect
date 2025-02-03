package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.Agent
import com.example.hybridconnect.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(agent: Agent) {
        authRepository.registerAgent(agent)
    }
}