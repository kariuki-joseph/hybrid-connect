package com.example.hybridconnect.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.usecase.CreateTransactionUseCase
import com.example.hybridconnect.domain.usecase.DecrementCustomerBalanceUseCase
import com.example.hybridconnect.domain.usecase.DialUssdUseCase
import com.example.hybridconnect.domain.usecase.GetCustomersUseCase
import com.example.hybridconnect.domain.usecase.GetOffersUseCase
import com.example.hybridconnect.domain.usecase.GetOrCreateCustomerUseCase
import com.example.hybridconnect.domain.usecase.ObserveTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

private const val TAG = "QuickDialViewModel"

@HiltViewModel
class QuickDialViewModel @Inject constructor(
    private val dialUssdUseCase: DialUssdUseCase,
    private val getCustomersUseCase: GetCustomersUseCase,
    private val getOffersUseCase: GetOffersUseCase,
    private val decrementCustomerBalanceUseCase: DecrementCustomerBalanceUseCase,
    private val getOrCreateCustomerUseCase: GetOrCreateCustomerUseCase,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val observeTransactionsUseCase: ObserveTransactionsUseCase,
) : ViewModel() {
    val customers: StateFlow<List<Customer>> = getCustomersUseCase.customer
    val offers: StateFlow<List<Offer>> = getOffersUseCase.offers

    private val _customerPhone = MutableStateFlow("")
    val customerPhone: StateFlow<String> = _customerPhone.asStateFlow()

    private val _isDialing = MutableStateFlow(false)
    val isDialing: StateFlow<Boolean> = _isDialing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _ussdDialSuccess = MutableStateFlow(false)
    val ussdDialSuccess: StateFlow<Boolean> = _ussdDialSuccess.asStateFlow()

    private val _selectedOffer = MutableStateFlow<Offer?>(null)
    val selectedOffer: StateFlow<Offer?> = _selectedOffer

    private val _ussdResponse = MutableStateFlow<String?>(null)
    val ussdResponse: StateFlow<String?> = _ussdResponse

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getCustomersUseCase()
            getOffersUseCase()
        }
    }

    fun onSelectOffer(offer: Offer?) {
        _selectedOffer.value = offer
    }

    fun onCustomerPhoneChanged(phone: String) {
        _customerPhone.value = phone
    }

    fun dial() {
        try {
            val customerPhone = "0" + _customerPhone.value.takeLast(9)
            val selectedOffer = _selectedOffer.value

            if (customerPhone.isEmpty()) {
                throw Exception("Customer phone cannot be empty")
            }
            if (customerPhone.length < 10) {
                throw Exception("Phone number too short")
            }
            if (selectedOffer == null) {
                throw Exception("You have not selected any offer")
            }

            viewModelScope.launch(Dispatchers.IO) {
                val customer = getOrCreateCustomerUseCase(customerPhone, customerPhone)
                val transaction = Transaction(
                    id = UUID.randomUUID(),
                    amount = selectedOffer.price,
                    customer = customer,
                    mpesaMessage = "Unavailable: Reason - QuickDial",
                    offer = selectedOffer,
                )
                createTransactionUseCase(transaction)
                _ussdDialSuccess.value = false
                _isDialing.value = true
                dialUssdUseCase(transaction)
                decrementCustomerBalanceUseCase(customer, selectedOffer.price)

                _ussdResponse.value = null
                observeTransactionStatus(transaction.id) { success, message ->
                    _isDialing.value = false
                    _ussdDialSuccess.value = success
                    _ussdResponse.value = message
                    if (success) {
                        _customerPhone.value = ""
                        _selectedOffer.value = null
                    }
                }
            }

        } catch (e: Exception) {
            _ussdDialSuccess.value = false
            _isDialing.value = false
            _errorMessage.value = e.message
        }
    }

    fun resetSnackbarMessage() {
        _errorMessage.value = null
    }

    fun resetUssdResponse(){
        _ussdResponse.value = null
    }

    private suspend fun observeTransactionStatus(
        transactionId: UUID,
        callback: (Boolean, String) -> Unit,
    ) {
        var isProcessed = false

        observeTransactionsUseCase.transactions
            .filter { transactions ->
                transactions.any { it.id == transactionId }
            }
            .map { transactions ->
                transactions.first { it.id == transactionId }
            }
            .collect { transaction ->
                if (isProcessed) {
                    return@collect
                }

                Log.d(TAG, transaction.toString())
                when (transaction.status) {
                    TransactionStatus.SUCCESS -> {
                        isProcessed = true
                        _isDialing.value = false
                        callback(true, "Success: ${transaction.responseMessage}")
                    }

                    TransactionStatus.FAILED -> {
                        isProcessed = true
                        _isDialing.value = false
                        callback(false, "Failed: ${transaction.responseMessage}")
                    }

                    else -> {}
                }
            }
    }
}