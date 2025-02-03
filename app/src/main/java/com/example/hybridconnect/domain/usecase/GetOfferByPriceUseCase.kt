package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.repository.OfferRepository
import javax.inject.Inject

class GetOfferByPriceUseCase @Inject constructor(
    private val offerRepository: OfferRepository,
) {
    suspend operator fun invoke(price: Int): Offer? {
        return offerRepository.getOfferByPrice(price)
    }
}