package com.example.hybridconnect.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.enums.SubscriptionType
import com.example.hybridconnect.domain.enums.TransactionType
import com.example.hybridconnect.domain.model.Agent
import com.example.hybridconnect.domain.model.SubscriptionPackage
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.repository.PaymentRepository
import com.example.hybridconnect.domain.repository.PrefsRepository
import com.example.hybridconnect.domain.repository.SubscriptionPlanRepository
import com.example.hybridconnect.domain.usecase.AddSubscriptionUseCase
import com.example.hybridconnect.domain.usecase.CreateTransactionUseCase
import com.example.hybridconnect.domain.usecase.DialUssdUseCase
import com.example.hybridconnect.domain.usecase.FormatUssdUseCase
import com.example.hybridconnect.domain.usecase.GetOrCreateCustomerUseCase
import com.example.hybridconnect.domain.usecase.ObserveTransactionsUseCase
import com.example.hybridconnect.domain.usecase.socket.ObserveWebsocketMessages
import com.example.hybridconnect.domain.usecase.subscriptions.GetSubscriptionPackageUseCase
import com.example.hybridconnect.domain.utils.Constants
import com.example.hybridconnect.domain.utils.isInsufficientBalanceResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

private const val TAG = "MakePaymentViewModel"

@HiltViewModel
class MakePaymentViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getSubscriptionPackageUseCase: GetSubscriptionPackageUseCase,
    private val observeWebsocketMessages: ObserveWebsocketMessages,
    private val paymentRepository: PaymentRepository,
    private val formatUssdUseCase: FormatUssdUseCase,
    private val dialUssdUseCase: DialUssdUseCase,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val observeTransactionsUseCase: ObserveTransactionsUseCase,
    private val getOrCreateCustomerUseCase: GetOrCreateCustomerUseCase,
    private val addSubscriptionUseCase: AddSubscriptionUseCase,
    private val subscriptionPlanRepository: SubscriptionPlanRepository,
    private val prefsRepository: PrefsRepository
) : ViewModel() {
    private val agent: StateFlow<Agent?> = authRepository.agent

    private val _mpesaNumber = MutableStateFlow("")
    val mpesaNumber: StateFlow<String> = _mpesaNumber.asStateFlow()

    private val _showConfirmationDialog = MutableStateFlow(false)
    val showConfirmationDialog: StateFlow<Boolean> = _showConfirmationDialog

    private val _showStkSuccessDialog = MutableStateFlow(false)
    val showStkSuccessDialog: StateFlow<Boolean> = _showStkSuccessDialog.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _payViaStkPush = MutableStateFlow(true)
    val payViaStkPush: StateFlow<Boolean> = _payViaStkPush.asStateFlow()

    private val _mpesaTextFieldError = MutableStateFlow(false)
    val mpesaTextFieldError: StateFlow<Boolean> = _mpesaTextFieldError.asStateFlow()

    private val _mpesaTextFieldErrorMessage = MutableStateFlow("")
    val mpesaTextFieldErrorMessage: StateFlow<String> = _mpesaTextFieldErrorMessage.asStateFlow()

    private val _isAwaitingPayment = MutableStateFlow(false)
    val isAwaitingPayment: StateFlow<Boolean> = _isAwaitingPayment

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _subscription = MutableStateFlow(paymentRepository.getSubscription())
    val subscriptionPackage: StateFlow<SubscriptionPackage?> = _subscription

    private val _subscriptionSuccess = MutableStateFlow(false)
    val subscriptionSuccess: StateFlow<Boolean> = _subscriptionSuccess

    private val _subscriptionMessage = MutableStateFlow("")
    val subscriptionMessage: StateFlow<String> = _subscriptionMessage

    init {
        getAgent()
    }

    fun onMpesaNumberChanged(number: String) {
        if (_mpesaTextFieldError.value) {
            _mpesaTextFieldError.value = false
            _mpesaTextFieldErrorMessage.value = ""
        }

        _mpesaNumber.value = number
    }

    fun onShowConfirmationDialogChanged(show: Boolean) {
        _showConfirmationDialog.value = show
        _isAwaitingPayment.value = false
    }

    fun onPayViaStkPushChanged(payViaStkPush: Boolean) {
        _payViaStkPush.value = payViaStkPush
    }

    fun onShowStkStatusDialogChanged(show: Boolean) {
        _showStkSuccessDialog.value = show
    }

    fun verifyInputs(): Boolean {
        if (mpesaNumber.value.isEmpty()) {
            _mpesaTextFieldError.value = true
            _mpesaTextFieldErrorMessage.value = "Mpesa number is required"
            return false
        }
        if (mpesaNumber.value.length <= 9 || mpesaNumber.value.length > 13) {
            _mpesaTextFieldError.value = true
            _mpesaTextFieldErrorMessage.value = "Mpesa number is invalid"
            return false
        }

        return true
    }


    private fun getAgent() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                authRepository.fetchAgent()
            }catch (e: Exception){
                _errorMessage.value = e.message.toString()
            }
        }

        agent.onEach { agent ->
            if (agent != null) {
                _mpesaNumber.value = agent.phoneNumber
            }
        }.launchIn(viewModelScope)
    }

    fun initiatePayment() {
        if (mpesaNumber.value.isEmpty()) {
            _mpesaTextFieldError.value = true
            _mpesaTextFieldErrorMessage.value = "Mpesa number is required"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val subscription = _subscription.value?.id?.let { getSubscriptionPackageUseCase(it) }
                ?: throw Exception("Please select subscription package first")
            try {
                _isLoading.value = true
//                subscribeToPackageUseCase(subscription, mpesaNumber.value)D
                _isLoading.value = false
                _showConfirmationDialog.value = false
                _showStkSuccessDialog.value = true

                observePaymentStatus { status ->
                    CoroutineScope(Dispatchers.IO).launch {
                        if (status == 1) {
                            Log.d(TAG, "Payment successful, adding subscription")
                            addSubscriptionUseCase(subscription)
                        } else {
                            Log.d(TAG, "Payment not successful. Cannot add to subscription")
                        }
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _isAwaitingPayment.value = false
                _showConfirmationDialog.value = false
                _mpesaTextFieldError.value = true
                _mpesaTextFieldErrorMessage.value =
                    e.message ?: "Error subscribing to package. Please try again"
                e.printStackTrace()
            }
        }
    }

    private fun observePaymentStatus(callback: (status: Int) -> Unit) {
        observeWebsocketMessages("PAYMENT_STATUS") { status ->
            Log.d(TAG, "Payment Status changed to $status")
            callback(status.toInt())
        }
    }

    fun payWithAirtime() {
        val subscription = subscriptionPackage.value
        val agent = agent.value

        if (subscription == null) {
            _errorMessage.value = "Please select a subscription plan first"
            return
        }
        if (agent == null) {
            _errorMessage.value = "You need to be logged in to renew subscription"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                _subscriptionSuccess.value = false
                val adminPhone = paymentRepository.getAdminSubscriptionNumber()
                val ussdCode =
                    formatUssdUseCase(Constants.AIRTIME_SUBSCRIPTION_USSD_CODE, adminPhone).replace(
                        "AMT",
                        subscription.price.toString()
                    )

                val customer = getOrCreateCustomerUseCase(agent.phoneNumber, "Me")
                val subscriptionTransaction = Transaction(
                    amount = subscription.price,
                    customer = customer,
                    type = TransactionType.SUBSCRIPTION_RENEWAL,
                    offer = null,
                )

                createTransactionUseCase(subscriptionTransaction)
                dialUssdUseCase(subscriptionTransaction)

                observeTransactionStatus(subscriptionTransaction.id) { success, message ->
                    _isLoading.value = false
                    _subscriptionSuccess.value = success
                    if (success) {
                        viewModelScope.launch(Dispatchers.IO) {
                            try {
                                addSubscriptionUseCase(subscription)
                                val updatedPlan = subscriptionPlanRepository.getActivePlans()
                                    .find { it.type == subscription.type }

                                if (updatedPlan != null) {
                                    _subscriptionMessage.value =
                                        getSubscriptionMessage(subscription, updatedPlan.limit)
                                }
                            } catch (e: Exception){
                                Log.e(TAG, e.message.toString())
                                _errorMessage.value = e.message
                            }
                        }

                    } else {
                        val errorMsg =
                            if (isInsufficientBalanceResponse(message)) "Insufficient airtime balance to complete your subscription" else message
                        _errorMessage.value = errorMsg
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                e.printStackTrace()
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }


    }

    fun resetSnackbarMessage() {
        _errorMessage.value = null
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
                        _isLoading.value = false
                        callback(true, "Subscription Successful: ${transaction.responseMessage}")
                    }

                    TransactionStatus.FAILED -> {
                        isProcessed = true
                        _isLoading.value = false
                        callback(false, "Subscription Failed: ${transaction.responseMessage}")
                    }

                    else -> {}
                }
            }
    }


    private fun getSubscriptionMessage(
        subscription: SubscriptionPackage,
        updatedLimit: Long,
    ): String {
        return when (subscription.type) {
            SubscriptionType.LIMITED -> {
                "Thank you for purchasing ${subscription.limit.toInt()} token${if (subscription.limit.toInt() > 1) "s" else ""}. " +
                        "Your new token balance is $updatedLimit."
            }
            else -> {
                val dateFormatter =
                    SimpleDateFormat("MMMM dd yyyy 'at' HH:mm:ss", Locale.getDefault())
                val formattedDate = dateFormatter.format(Date(updatedLimit))
                "Thank you for subscribing to ${subscription.name}. This plan will will be active till $formattedDate"
            }
        }
    }

}