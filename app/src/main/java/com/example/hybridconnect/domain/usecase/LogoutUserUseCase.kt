package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.repository.SettingsRepository
import javax.inject.Inject

private const val TAG = "LogoutUserUseCase"

class LogoutUserUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke() {
        try {
            val agent = authRepository.fetchAgent()
            authRepository.logoutUser(agent)
            settingsRepository.saveSetting(AppSetting.AGENT_ID, "")
        } catch (e: Exception) {
            Log.e(TAG, "logoutUser", e)
            throw e
        }
    }
}