package com.example.hybridconnect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.enums.SiteLinkAccountType
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.SiteLink
import com.example.hybridconnect.domain.repository.PaymentRepository
import com.example.hybridconnect.domain.repository.PrefsRepository
import com.example.hybridconnect.domain.usecase.ActivateSiteLinkUseCase
import com.example.hybridconnect.domain.usecase.AddSiteLinkOfferUseCase
import com.example.hybridconnect.domain.usecase.DeleteSiteLinkOfferUseCase
import com.example.hybridconnect.domain.usecase.DeleteSiteLinkUseCase
import com.example.hybridconnect.domain.usecase.GetOffersUseCase
import com.example.hybridconnect.domain.usecase.GetSiteLinkUseCase
import com.example.hybridconnect.domain.usecase.RequestSiteLinkUseCase
import com.example.hybridconnect.domain.usecase.UpdateSiteLinkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SiteLinkViewModel @Inject constructor(
    private val getSiteLinkUseCase: GetSiteLinkUseCase,
    private val requestSiteLinkUseCase: RequestSiteLinkUseCase,
    private val getOffersUseCase: GetOffersUseCase,
    private val addSiteLinkOfferUseCase: AddSiteLinkOfferUseCase,
    private val deleteSiteLinkOfferUseCase: DeleteSiteLinkOfferUseCase,
    private val prefsRepository: PrefsRepository,
    private val activateSiteLinkUseCase: ActivateSiteLinkUseCase,
    private val updateSiteLinkUseCase: UpdateSiteLinkUseCase,
    private val deleteSiteLinkUseCase: DeleteSiteLinkUseCase,
    private val paymentRepository: PaymentRepository,
) : ViewModel() {
    val siteLink: StateFlow<SiteLink?> = getSiteLinkUseCase.siteLink
    val offers: StateFlow<List<Offer>> = getOffersUseCase.offers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _siteLinkGenerationSuccess = MutableStateFlow(false)
    val siteLinkGenerationSuccess: StateFlow<Boolean> = _siteLinkGenerationSuccess.asStateFlow()

    private val _siteLinkUpdateSuccess = MutableStateFlow(false)
    val siteLinkUpdateSuccess: StateFlow<Boolean> = _siteLinkUpdateSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess.asStateFlow()

    private val _siteName = MutableStateFlow("")
    val siteName: StateFlow<String> = _siteName.asStateFlow()

    private val _accountNumber = MutableStateFlow("")
    val accountNumber: StateFlow<String> = _accountNumber.asStateFlow()

    private val _accountType = MutableStateFlow(SiteLinkAccountType.TILL)
    val accountType: StateFlow<SiteLinkAccountType> = _accountType.asStateFlow()

    private val _isSiteLinkActive = MutableStateFlow(false)
    val isSiteLinkActive: StateFlow<Boolean> = _isSiteLinkActive.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getSiteLinkUseCase()
            getOffersUseCase()
        }
        getSiteLinkActiveStatus()
        getSiteLinkPhoneNumbers()
    }

    private fun getSiteLinkActiveStatus() {
        viewModelScope.launch {
            try {
                siteLink.collect { siteLinkValue ->
                    val isActive = siteLinkValue?.isActive ?: false
                    prefsRepository.saveSetting(AppSetting.PROCESS_SITE_LINK_MESSAGES, isActive.toString())
                    _isSiteLinkActive.value = isActive
                }
            } catch (e: Exception) {
                _isSiteLinkActive.value = false
            }
        }
    }

    private fun getSiteLinkPhoneNumbers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                paymentRepository.getAdminSiteLinkNumber()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun onSiteLinkActiveStatusChanged(status: Boolean) {
        viewModelScope.launch {
            try {
                _isSiteLinkActive.value = status
                activateSiteLinkUseCase(status)
                _successMessage.value =
                    "SiteLink ${if (status) "activated" else "deactivated"} successfully"
            } catch (e: Exception) {
                _isSiteLinkActive.value = !status
                _errorMessage.value = e.message
            }
        }
    }

    fun onSiteNameChanged(name: String) {
        _siteName.value = name
    }

    fun onAccountNumberChanged(acc: String) {
        _accountNumber.value = acc
    }

    fun onAccountTypeChanged(acc: SiteLinkAccountType) {
        _accountType.value = acc
    }

    fun getAndFillSiteLinkDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getSiteLinkUseCase().collect { site ->
                    site?.let {
                        _siteName.value = site.siteName
                        _accountNumber.value = site.accountNumber
                        _accountType.value = site.accountType
                    }
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message.toString()
            }
        }
    }

    fun requestSiteLink() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val siteName = _siteName.value
                val accountNumber = _accountNumber.value

                val isTill = accountType.value == SiteLinkAccountType.TILL
                val isMpesa = accountType.value == SiteLinkAccountType.MPESA

                if (siteName.isEmpty()) {
                    throw Exception("Site Name cannot be empty")
                }
                if (siteName.length <= 3) {
                    throw Exception("Site Name too short")
                }
                if (accountNumber.isEmpty()) {
                    if (isTill) {
                        throw Exception("Till Number cannot be empty")
                    } else {
                        throw Exception("M-Pesa Number cannot be empty")
                    }
                }

                if (isMpesa && accountNumber.length < 10) {
                    throw Exception("M-Pesa number too short")
                }
                if (isMpesa && accountNumber.length > 14) {
                    throw Exception("M-Pesa Number too long")
                }

                withContext(Dispatchers.IO) {
                    requestSiteLinkUseCase(
                        siteName = siteName,
                        accountNumber = accountNumber,
                        accountType = accountType.value
                    )
                }
                _isLoading.value = false
                _siteLinkGenerationSuccess.value = true
                _successMessage.value = "SiteLink generated successfully"
            } catch (e: Exception) {
                _isLoading.value = false
                _siteLinkGenerationSuccess.value = false
                _errorMessage.value = e.message
            }
        }
    }

    fun updateSiteLink() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val siteName = _siteName.value
                val accountNumber = _accountNumber.value
                val isTill = accountType.value == SiteLinkAccountType.TILL
                val isMpesa = accountType.value == SiteLinkAccountType.MPESA

                if (siteName.isEmpty()) {
                    throw Exception("Site Name cannot be empty")
                }
                if (siteName.length <= 3) {
                    throw Exception("Site Name too short")
                }
                if (accountNumber.isEmpty()) {
                    if (isTill) {
                        throw Exception("Till Number cannot be empty")
                    } else {
                        throw Exception("M-Pesa Number cannot be empty")
                    }
                }

                if (isMpesa && accountNumber.length < 10) {
                    throw Exception("M-Pesa number too short")
                }
                if (isMpesa && accountNumber.length > 14) {
                    throw Exception("M-Pesa Number too long")
                }

                val siteLink = siteLink.value
                    ?: throw Exception("You don't seem to have a SiteLink. Please create one then try again")
                withContext(Dispatchers.IO) {
                    val updatedSiteLink = siteLink.copy(
                        siteName = siteName,
                        accountNumber = accountNumber,
                        accountType = accountType.value
                    )

                    updateSiteLinkUseCase(updatedSiteLink)
                    _isLoading.value = false
                    _siteLinkUpdateSuccess.value = true
                    _successMessage.value = "SiteLink updated successfully"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _siteLinkUpdateSuccess.value = false
                _errorMessage.value = e.message
            }
        }
    }

    fun deleteSiteLink() {
        viewModelScope.launch {
            try {
                _isDeleting.value = true
                val currentSiteLink = siteLink.value
                    ?: throw Exception("No SiteLink found to delete.")

                withContext(Dispatchers.IO) {
                    deleteSiteLinkUseCase(currentSiteLink)
                }

                _isDeleting.value = false
                _deleteSuccess.value = true
                _successMessage.value = "SiteLink deleted successfully"
            } catch (e: Exception) {
                _isDeleting.value = false
                _deleteSuccess.value = false
                _errorMessage.value = e.message ?: "Failed to delete SiteLink"
            }
        }
    }


    fun addSiteLinkOffer(offer: Offer) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    addSiteLinkOfferUseCase(offer)
                    getOffersUseCase()
                }
                _successMessage.value = "Offer added successfully"
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message
            }
        }
    }

    fun removeSiteLinkOffer(offer: Offer) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    deleteSiteLinkOfferUseCase(offer)
                    getOffersUseCase()
                }
                _successMessage.value = "Offer removed successfully"

            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message
            }
        }
    }

    fun resetSnackbarMessage() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun resetDeleteSuccess() {
        _deleteSuccess.value = false
    }
}