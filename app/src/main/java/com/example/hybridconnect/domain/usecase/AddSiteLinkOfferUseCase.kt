package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.repository.OfferRepository
import com.example.hybridconnect.domain.repository.SiteLinkRepository
import javax.inject.Inject

private const val TAG = "AddSiteLinkOfferUseCase"

class AddSiteLinkOfferUseCase @Inject constructor(
    private val siteLinkRepository: SiteLinkRepository,
    private val offerRepository: OfferRepository
) {
    suspend operator fun invoke(offer: Offer) {
        try {
            offerRepository.updateOffer(offer.copy(isSiteLinked = true))
            siteLinkRepository.addSiteLinkOffer(offer)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            offerRepository.updateOffer(offer.copy(isSiteLinked = false))
            e.printStackTrace()
            throw e
        }
    }
}