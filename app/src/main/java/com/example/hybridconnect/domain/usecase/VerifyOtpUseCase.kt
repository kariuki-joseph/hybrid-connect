package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.repository.PrefsRepository
import javax.inject.Inject

private const val TAG = "VerifyOtpUseCase"

class VerifyOtpUseCase @Inject constructor(
    private val prefsRepository: PrefsRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(otp: String) {
        try {
            val agent = authRepository.verifyOtp(otp)
            authRepository.saveAgent(agent)
            prefsRepository.saveSetting(AppSetting.AGENT_ID, agent.id.toString())

        } catch (e: Exception) {
            Log.e(TAG, "verifyOtpUseCase", e)
            throw e
        }

    }
}