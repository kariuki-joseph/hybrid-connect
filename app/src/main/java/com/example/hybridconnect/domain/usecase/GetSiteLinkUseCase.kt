package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.SiteLink
import com.example.hybridconnect.domain.repository.SiteLinkRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetSiteLinkUseCase @Inject constructor(
    private val siteLinkRepository: SiteLinkRepository,
) {
    val siteLink: StateFlow<SiteLink?> = siteLinkRepository.siteLink

    suspend operator fun invoke(): StateFlow<SiteLink?> {
        siteLinkRepository.getSavedSiteLink()
        return siteLinkRepository.siteLink
    }
}