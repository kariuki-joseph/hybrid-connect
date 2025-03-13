package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.repository.SettingsRepository
import javax.inject.Inject

class GetAppStatusUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Boolean {
        return settingsRepository.isAppActive()
    }
}