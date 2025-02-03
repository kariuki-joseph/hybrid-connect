package com.example.hybridconnect.domain.repository

import com.example.hybridconnect.domain.model.Offer
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

interface OfferRepository {
    val offers: StateFlow<List<Offer>>

    suspend fun addOffer(offer: Offer)

    suspend fun getAllOffers(): List<Offer>

    suspend fun getOfferByPrice(price: Int): Offer?

    suspend fun getOfferById(id: UUID): Offer?

    suspend fun deleteOffer(offer: Offer)

    suspend fun updateOffer(updatedOffer: Offer)
}
