package com.example.hybridconnect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.enums.RenewInterval
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.enums.RescheduleMode
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.RescheduleInfo
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.usecase.CreateTransactionUseCase
import com.example.hybridconnect.domain.usecase.DecrementCustomerBalanceUseCase
import com.example.hybridconnect.domain.usecase.DeleteTransactionUseCase
import com.example.hybridconnect.domain.usecase.DialUssdUseCase
import com.example.hybridconnect.domain.usecase.FormatUssdUseCase
import com.example.hybridconnect.domain.usecase.GetCustomerBalanceUseCase
import com.example.hybridconnect.domain.usecase.GetTransactionUseCase
import com.example.hybridconnect.domain.usecase.ObserveOffersUseCase
import com.example.hybridconnect.domain.usecase.RescheduleTransactionUseCase
import com.example.hybridconnect.domain.usecase.UpdateTransactionStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RescheduleViewModel @Inject constructor(
    private val dialUssdUseCase: DialUssdUseCase,
    private val formatUssdUseCase: FormatUssdUseCase,
    private val observeOffersUseCase: ObserveOffersUseCase,
    private val getTransactionUseCase: GetTransactionUseCase,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val decrementCustomerBalanceUseCase: DecrementCustomerBalanceUseCase,
    private val getCustomerBalanceUseCase: GetCustomerBalanceUseCase,
    private val updateTransactionStatusUseCase: UpdateTransactionStatusUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val rescheduleTransactionUseCase: RescheduleTransactionUseCase
) : ViewModel() {
    val offers: StateFlow<List<Offer>> = observeOffersUseCase.offers

    private val _transaction: MutableStateFlow<Transaction?> = MutableStateFlow<Transaction?>(null)
    val transaction: StateFlow<Transaction?> = _transaction.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _rescheduleSuccess = MutableStateFlow(false)
    val rescheduleSuccess: StateFlow<Boolean> = _rescheduleSuccess.asStateFlow()
    private var renewCount = 0
    private var renewInterval = RenewInterval.DAY

    init {
        loadOffers()
    }

    private fun loadOffers() {
        viewModelScope.launch {
            observeOffersUseCase.refreshOffers()
        }
    }

    fun resetSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun getTransaction(transactionId: String) {
        viewModelScope.launch {
            try {
                _transaction.value = getTransactionUseCase(UUID.fromString(transactionId))
            } catch (e: Exception) {
                _snackbarMessage.value = e.message
            }
        }
    }

    fun rescheduleOffer(originalTransaction: Transaction?, offer: Offer?, time: Long) {
        if(originalTransaction == null){
            _snackbarMessage.value = "Original transaction not found. Please go back and try again"
            return
        }

        if (offer == null) {
            _snackbarMessage.value = "Please select an offer first"
            return
        }

        val customer = originalTransaction.customer
        val ussdCode = formatUssdUseCase(offer.ussdCode, customer.phone)

        val autoRenew = renewCount != 0

        if (!autoRenew) {
            viewModelScope.launch {
                try {
                    var customerBalance = getCustomerBalanceUseCase(customer)

                    if (customerBalance < offer.price) {
                        throw Exception("Insufficient funds. Available customer account balance is Ksh. $customerBalance")
                    }
                    rescheduleTransactionUseCase(originalTransaction, offer, time)
                    customerBalance -= offer.price
                    _rescheduleSuccess.value = true
                    _snackbarMessage.value = "Offer rescheduled successfully"
                } catch (e: Exception) {
                    _rescheduleSuccess.value = false
                    _snackbarMessage.value = e.message
                }
            }
            return
        }

        val calendar = Calendar.getInstance().apply { timeInMillis = time }

        viewModelScope.launch {
            try {
                var customerBalance = getCustomerBalanceUseCase(customer)

                for (i in 0 until renewCount) {
                    if (customerBalance < offer.price) {
                        throw Exception("Customer balance too low to schedule the selected offer. Customer balance: Ksh. $customerBalance")
                    }

                    val scheduledTransaction = Transaction(
                        id = UUID.randomUUID(),
                        amount = offer.price,
                        time = originalTransaction.time,
                        mpesaMessage = originalTransaction.mpesaMessage,
                        responseMessage = "",
                        status = TransactionStatus.SCHEDULED,
                        customer = customer,
                        offer = offer,
                        rescheduleInfo = RescheduleInfo(
                            parentTransactionId = originalTransaction.id,
                            time = calendar.timeInMillis,
                            rescheduleMode = RescheduleMode.AUTO_RENEW
                        )
                    )

                    createTransactionUseCase(scheduledTransaction)
                    dialUssdUseCase(scheduledTransaction,
                        calendar.timeInMillis,
                    )
                    decrementCustomerBalanceUseCase(customer, offer.price)
                    customerBalance -= offer.price
                    when (renewInterval) {
                        RenewInterval.MINUTE -> calendar.add(Calendar.MINUTE, 1)
                        RenewInterval.HOUR -> calendar.add(Calendar.HOUR, 1)
                        RenewInterval.DAY -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                        else -> calendar.add(Calendar.MONTH, 1)
                    }
                }
                updateTransactionStatusUseCase(originalTransaction.id, TransactionStatus.RESCHEDULED)
                _rescheduleSuccess.value = true
                _snackbarMessage.value = "Offer rescheduled successfully"
            } catch (e: Exception) {
                _rescheduleSuccess.value = false
                _snackbarMessage.value = e.message
            }
        }
    }

    fun onAutoRenew(renewCount: Int, interval: RenewInterval) {
        this.renewCount = renewCount
        this.renewInterval = interval
    }
}