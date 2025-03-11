package com.example.hybridconnect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.model.ConnectedApp
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.repository.ConnectedAppRepository
import com.example.hybridconnect.domain.repository.OfferRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AppDetailsViewModel @Inject constructor(
    private val connectedAppRepository: ConnectedAppRepository,
    private val offerRepository: OfferRepository,
) : ViewModel() {

    private val _connectedApp = MutableStateFlow<ConnectedApp?>(null)
    val connectedApp: StateFlow<ConnectedApp?> = _connectedApp.asStateFlow()

    private val _availableOffers = MutableStateFlow<List<Offer>>(emptyList())
    val availableOffers: StateFlow<List<Offer>> = _availableOffers.asStateFlow()

    private val _selectedOffers = MutableStateFlow<Set<UUID>>(emptySet())
    val selectedOffers: StateFlow<Set<UUID>> = _selectedOffers.asStateFlow()

    private val _connectedOffers = MutableStateFlow<Set<UUID>>(emptySet())
    val connectedOffers: StateFlow<Set<UUID>> = _connectedOffers

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    fun loadConnectedApp(connectId: String) {
        viewModelScope.launch {
            val connectedAppDeferred = async { connectedAppRepository.getConnectedApp(connectId) }
            val availableOffersDeferred = async { offerRepository.getAllOffers() }
            val connectedOffersDeferred = async { loadConnectedOffers(connectId) }
            _connectedApp.value = connectedAppDeferred.await()
            _availableOffers.value = availableOffersDeferred.await()

            connectedOffersDeferred.await()
        }
    }

    private fun loadConnectedOffers(connectId: String) {
        viewModelScope.launch {
            _connectedOffers.value =
                connectedAppRepository.getConnectedOffers(connectId).map { it.id }.toSet()
        }
    }

    fun toggleOfferSelection(offerId: UUID, isSelected: Boolean) {
        viewModelScope.launch {
            try {
                val app = _connectedApp.value ?: return@launch
                val offer = _availableOffers.value.find { it.id == offerId }
                    ?: throw Exception("Offer not found")

                if (isSelected) {
                    connectedAppRepository.addOffer(app, offer)
                } else {
                    connectedAppRepository.deleteOffer(app, offer)
                }

                loadConnectedOffers(app.connectId)
            } catch (e: Exception) {
                _snackbarMessage.value = e.message
            }
        }
    }

    fun saveSelectedOffers() {
        val app = _connectedApp.value ?: return
        viewModelScope.launch {
            val currentOffers = _availableOffers.value.map { it.id }.toSet()
            val newSelected = _selectedOffers.value

            // Remove unselected offers
            currentOffers.filter { it !in newSelected }.forEach { offerId ->
                val offer = _availableOffers.value.find { it.id == offerId }
                if (offer != null) {
                    connectedAppRepository.deleteOffer(app, offer)
                }
            }

            // Add newly selected offers
            newSelected.filter { it !in currentOffers }.forEach { offerId ->
                val offer = _availableOffers.value.find { it.id == offerId }
                if (offer != null) {
                    connectedAppRepository.addOffer(app, offer)
                }
            }
        }
    }

    fun deleteConnectedApp() {
        val app = _connectedApp.value ?: return
        viewModelScope.launch {
            connectedAppRepository.deleteConnectedApp(app)
        }
    }

    fun resetSnackbarMessage() {
        _snackbarMessage.value = null
    }
}
