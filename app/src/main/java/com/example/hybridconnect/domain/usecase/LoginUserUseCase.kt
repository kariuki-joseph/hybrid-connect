package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.model.AuthDetails
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.repository.PrefsRepository
import javax.inject.Inject

private const val TAG = "LoginUserUseCase"

class LoginUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val prefsRepository: PrefsRepository,
) {
    suspend operator fun invoke(email: String, pin: String): AuthDetails {
        try {
            val authDetails = authRepository.loginUser(email, pin)
            authRepository.saveAgent(authDetails.agent)
            prefsRepository.saveAccessToken(authDetails.token)
            prefsRepository.saveSetting(AppSetting.AGENT_ID, authDetails.agent.id.toString())
            return authDetails
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            e.printStackTrace()
            throw e
        }
    }
}