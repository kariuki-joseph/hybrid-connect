package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.repository.OfferRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveOffersUseCase @Inject constructor(
    private val offerRepository: OfferRepository
) {
    val offers: StateFlow<List<Offer>> = offerRepository.offers

    suspend fun refreshOffers(){
        offerRepository.getAllOffers()
    }
}