package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.repository.OfferRepository
import javax.inject.Inject

class AddOfferUseCase @Inject constructor(
    private val offerRepository: OfferRepository
) {
    suspend operator fun invoke(offer: Offer){
        offerRepository.addOffer(offer)
    }
}