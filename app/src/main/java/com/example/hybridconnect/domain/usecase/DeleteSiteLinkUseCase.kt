package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.model.SiteLink
import com.example.hybridconnect.domain.repository.OfferRepository
import com.example.hybridconnect.domain.repository.SiteLinkRepository
import javax.inject.Inject

private const val TAG = "DeleteSiteLinkUseCase"
class DeleteSiteLinkUseCase @Inject constructor(
    private val siteLinkRepository: SiteLinkRepository,
    private val offerRepository: OfferRepository,
) {
    suspend operator fun invoke(siteLink: SiteLink) {
        try {
            siteLinkRepository.deleteSiteLinkRemote(siteLink)
            siteLinkRepository.deleteSiteLinkLocal(siteLink)

            offerRepository.getAllOffers()
                .filter { it.isSiteLinked }
                .forEach { offerRepository.updateOffer(it.copy(isSiteLinked = false)) }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            throw e
        }
    }
}