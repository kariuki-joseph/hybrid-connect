package com.example.hybridconnect.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.enums.OfferType
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.repository.OfferRepository
import com.example.hybridconnect.domain.usecase.AddOfferUseCase
import com.example.hybridconnect.domain.usecase.GetOfferByPriceUseCase
import com.example.hybridconnect.domain.usecase.GetOffersUseCase
import com.example.hybridconnect.domain.utils.NavigationManager
import com.example.hybridconnect.presentation.dto.OfferDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

private const val TAG = "OffersViewModel"

@HiltViewModel
class OffersViewModel @Inject constructor(
    private val getOffersUseCase: GetOffersUseCase,
    private val getOfferByPriceUseCase: GetOfferByPriceUseCase,
    private val addOffersUseCase: AddOfferUseCase,
    private val offerRepository: OfferRepository,
    private val sharedOffersRepository: SharedOffersRepository,
) : ViewModel() {
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    private val _addOfferSuccess = MutableStateFlow(false)
    val addOfferSuccess: StateFlow<Boolean> = _addOfferSuccess

    private val _editOfferSuccess = MutableStateFlow(false)
    val editOfferSuccess: StateFlow<Boolean> = _editOfferSuccess

    private val _selectedOffer = MutableStateFlow<Offer?>(null)

    private val _offerDetails = MutableStateFlow(OfferDetails())
    val offerDetails: StateFlow<OfferDetails> = _offerDetails

    private val _selectedOfferType = MutableStateFlow<OfferType>(OfferType.NONE)
    val selectedOfferType: StateFlow<OfferType> = _selectedOfferType

    private val _filteredOffers = MutableStateFlow<List<Offer>>(emptyList())
    val filteredOffers: StateFlow<List<Offer>> = _filteredOffers

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess.asStateFlow()

    init {
        getAllOffers()
        observeFilteredOffers()
    }

    private fun getAllOffers() {
        viewModelScope.launch {
            getOffersUseCase()
        }
    }

    private fun observeFilteredOffers() {
        combine(getOffersUseCase.offers, selectedOfferType) { offers, offerType ->
            filterOffers(offers, offerType)
        }.onEach { filtered ->
            _filteredOffers.value = filtered
        }.launchIn(viewModelScope)
    }

    private fun filterOffers(offers: List<Offer>, offerType: OfferType): List<Offer> {
        return when (offerType) {
            OfferType.NONE -> offers
            OfferType.DATA -> offers.filter { it.type == OfferType.DATA }
            OfferType.VOICE -> offers.filter { it.type == OfferType.VOICE }
            OfferType.SMS -> offers.filter { it.type == OfferType.SMS }
        }
    }

    fun setOfferTypeToAdd(offerType: OfferType) {
        sharedOffersRepository.setOfferTypeToAdd(offerType)
    }

    fun onOfferTypeSelected(offerType: OfferType) {
        _selectedOfferType.value = offerType
    }

    fun getOfferById(offerId: UUID) {
        val offer = getOffersUseCase.offers.value.find { it.id == offerId }
        _selectedOffer.value = offer
        offer?.let {
            _offerDetails.value = OfferDetails(
                offerName = it.name,
                ussdCode = it.ussdCode,
                price = it.price.toString()
            )
        }
    }

    fun updateOfferDetails(newDetails: OfferDetails) {
        _offerDetails.value = newDetails
    }

    fun saveOffer() {
        val newOffer = Offer(
            id = UUID.randomUUID(),
            name = _offerDetails.value.offerName,
            ussdCode = _offerDetails.value.ussdCode,
            price = _offerDetails.value.price.toIntOrNull() ?: 0,
            type = sharedOffersRepository.getOfferTypeToAdd()
        )

        viewModelScope.launch {
            try {
                val existingOffer = getOfferByPriceUseCase(newOffer.price)
                if (existingOffer != null) {
                    throw Exception("An offer with similar amount exists. Please try a different amount")
                }

                addOffersUseCase(newOffer)
                _addOfferSuccess.value = true
                _snackbarMessage.value = "Offer added successfully"
                clearOfferFields()
            } catch (e: Exception) {
                _snackbarMessage.value = e.message
                _addOfferSuccess.value = false
            }
        }
    }

    fun updateOffer() {
        viewModelScope.launch {
            _selectedOffer.value.let {
                if (it == null) return@launch

                val updatedOffer = it.copy(
                    name = _offerDetails.value.offerName,
                    ussdCode = _offerDetails.value.ussdCode,
                    price = _offerDetails.value.price.toIntOrNull() ?: 0,
                )
                viewModelScope.launch {
                    try {
                        val existingOffer = getOfferByPriceUseCase(updatedOffer.price)
                        if (existingOffer != null && existingOffer.id != updatedOffer.id) {
                            throw Exception("An offer with similar amount exists. Please try a different amount")
                        }
                        offerRepository.updateOffer(updatedOffer)
                        _editOfferSuccess.value = true
                        _snackbarMessage.value = "Offer updated successfully"
                        NavigationManager.back()
                        clearOfferFields()
                    } catch (e: Exception) {
                        _editOfferSuccess.value = false
                        _snackbarMessage.value = e.message
                    }
                }

            }
        }
    }

    private fun clearOfferFields() {
        _offerDetails.value = OfferDetails()
    }

    fun deleteOffer() {
        _selectedOffer.value?.let { offer ->
            viewModelScope.launch {
                try {
                    _deleteSuccess.value = false
                    _isDeleting.value = true
                    offerRepository.deleteOffer(offer)
                    _deleteSuccess.value = true
                    _snackbarMessage.value = "Offer deleted successfully"
                    NavigationManager.back()
                } catch (e: Exception){
                    _deleteSuccess.value = false
                    _snackbarMessage.value = e.message.toString()
                } finally {
                    _isDeleting.value = false
                }
            }
        }
    }

    fun resetSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun resetDeleteSuccess(){
        _deleteSuccess.value = false
    }
}