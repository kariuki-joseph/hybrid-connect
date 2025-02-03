package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.model.SiteLink
import com.example.hybridconnect.domain.repository.OfferRepository
import com.example.hybridconnect.domain.repository.PaymentRepository
import com.example.hybridconnect.domain.repository.SiteLinkRepository
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "LoginCoordinator"

@Singleton
class LoginCoordinator @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val siteLinkRepository: SiteLinkRepository,
    private val offerRepository: OfferRepository,
    private val paymentRepository: PaymentRepository,
) {
    suspend operator fun invoke(email: String, pin: String) {
        try {
            val authDetails = loginUserUseCase(email, pin)
            val siteLink = authDetails.siteLink
            Log.d(TAG, "siteLink: $siteLink")
            siteLink?.let {
                siteLinkRepository.updateSiteLinkLocal(it)
                syncOnlineOffers(it)
            }
            syncPaymentInformation()
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun syncOnlineOffers(siteLink: SiteLink) {
        val siteLinkOffers = siteLinkRepository.getSiteLinkOffers(siteLink)
        val localOffers = offerRepository.getAllOffers()

        Log.d(TAG, "Local offers: $localOffers")

        // Split the offers into two sets: one with tags, and one without
        val onlineOffersWithTags = siteLinkOffers.filter { it.tag != null }.toSet()
        val onlineOffersWithoutTags = siteLinkOffers.filter { it.tag == null }.toSet()

        // Process offers with tags first
        onlineOffersWithTags.forEach { offer ->
            Log.d(TAG, "processing siteLink offer with tag: $offer")
            val localOffer = localOffers.find { it.tag == offer.tag }
            if (localOffer != null) {
                Log.d(TAG, "Local offer that is online found $localOffer")
                offerRepository.updateOffer(offer.copy(isSiteLinked = true))
            } else {
                offerRepository.addOffer(offer.copy(isSiteLinked = true))
            }
        }

        // Process offers without tags, using IDs for matching
        onlineOffersWithoutTags.forEach { offer ->
            Log.d(TAG, "processing siteLink offer without tag: $offer")
            val localOffer = localOffers.find { it.id == offer.id }
            if (localOffer != null) {
                offerRepository.updateOffer(offer.copy(isSiteLinked = true))
            } else {
                offerRepository.addOffer(offer.copy(isSiteLinked = true))
            }
        }
    }



    private suspend fun syncPaymentInformation() {
        try {
            paymentRepository.getAdminSiteLinkNumber()
            paymentRepository.getAdminSubscriptionNumber()
        } catch (e: Exception) {
            Log.d(TAG, e.message, e)
        }
    }
}
