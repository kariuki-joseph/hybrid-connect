package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.local.dao.OfferDao
import com.example.hybridconnect.data.mappers.toDomain
import com.example.hybridconnect.data.mappers.toEntity
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.repository.OfferRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

private const val TAG = "OfferRepositoryImpl"

class OfferRepositoryImpl(
    private val offerDao: OfferDao,
) : OfferRepository {
    private val _offers = MutableStateFlow<List<Offer>>(emptyList())
    override val offers: StateFlow<List<Offer>> = _offers.asStateFlow()

    override suspend fun addOffer(offer: Offer) {
        offerDao.insert(offer.toEntity())
        _offers.update { currentOffers -> currentOffers + offer }
    }

    override suspend fun getOfferByPrice(price: Int): Offer? {
        return offerDao.getOfferByPrice(price)?.toDomain()
    }

    override suspend fun getOfferById(id: UUID): Offer? {
        return offerDao.getOfferById(id)?.toDomain()
    }

    override suspend fun getAllOffers(): List<Offer> {
        try {
            val localOffers = offerDao.getAllOffers()
            val offers = localOffers.map { it.toDomain() }
            _offers.value = offers
            return offers
        } catch (e: Exception) {
            _offers.value = emptyList()
            throw e
        }
    }

    override suspend fun deleteOffer(offer: Offer) {
        offerDao.deleteOfferById(offer.id)
        _offers.update { currentOffers -> currentOffers.filter { it.id != offer.id } }
    }


    override suspend fun updateOffer(updatedOffer: Offer) {
        Log.d(TAG, "Updating local offer to $updatedOffer")
        offerDao.update(updatedOffer.toEntity())
        _offers.update { currentOffers ->
            currentOffers.map { if (it.id == updatedOffer.id) updatedOffer else it }
        }
    }
}