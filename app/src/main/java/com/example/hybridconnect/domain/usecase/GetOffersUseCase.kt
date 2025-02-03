package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.repository.OfferRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetOffersUseCase @Inject constructor(
    private val offerRepository: OfferRepository,
) {
    val offers: StateFlow<List<Offer>> = offerRepository.offers
    suspend operator fun invoke(): StateFlow<List<Offer>> {
        offerRepository.getAllOffers()
        return offerRepository.offers
    }
}