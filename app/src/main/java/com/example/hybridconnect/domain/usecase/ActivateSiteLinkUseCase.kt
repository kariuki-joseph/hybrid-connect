package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.repository.SettingsRepository
import com.example.hybridconnect.domain.repository.SiteLinkRepository
import javax.inject.Inject

class ActivateSiteLinkUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val siteLinkRepository: SiteLinkRepository
) {
    suspend operator fun invoke(isActive: Boolean) {
        try {
            if (isActive) {
                siteLinkRepository.activateSiteLink()
                settingsRepository.saveSetting(AppSetting.PROCESS_SITE_LINK_MESSAGES, true.toString())
                siteLinkRepository.getSavedSiteLink()?.let {
                    siteLinkRepository.updateSiteLinkLocal(it.copy(isActive = true))
                }
            } else {
                siteLinkRepository.deactivateSiteLink()
                settingsRepository.saveSetting(AppSetting.PROCESS_SITE_LINK_MESSAGES, false.toString())
                siteLinkRepository.getSavedSiteLink()?.let {
                    siteLinkRepository.updateSiteLinkLocal(it.copy(isActive = false))
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }
}