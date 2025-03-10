package com.example.hybridconnect.presentation.viewmodel

import com.example.hybridconnect.domain.enums.OfferType
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedOffersRepository @Inject constructor() {
    private val _offerTypeToAdd = MutableStateFlow(OfferType.DATA)

    fun setOfferTypeToAdd(offerType: OfferType) {
        _offerTypeToAdd.value = offerType
    }

    fun getOfferTypeToAdd(): OfferType {
        return _offerTypeToAdd.value
    }
}