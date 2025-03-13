package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.repository.SettingsRepository
import javax.inject.Inject

class ActivateAppUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(){
        settingsRepository.setAppActive(true)
    }
}