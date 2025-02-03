package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.enums.SiteLinkAccountType
import com.example.hybridconnect.domain.repository.SiteLinkRepository
import javax.inject.Inject

class RequestSiteLinkUseCase @Inject constructor(
    private val siteLinkRepository: SiteLinkRepository,
) {
    suspend operator fun invoke(siteName: String, accountNumber: String, accountType: SiteLinkAccountType) {
        siteLinkRepository.requestSiteLink(siteName, accountNumber, accountType)
    }
}