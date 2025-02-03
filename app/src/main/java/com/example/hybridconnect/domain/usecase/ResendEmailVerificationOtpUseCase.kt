package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.repository.AuthRepository
import javax.inject.Inject

class ResendEmailVerificationOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String) {
        authRepository.sendOtp(email)
    }
}