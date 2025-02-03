package com.example.hybridconnect.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.enums.TransactionType
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.usecase.ObserveTransactionsUseCase
import com.example.hybridconnect.domain.usecase.RetryTransactionUseCase
import com.example.hybridconnect.presentation.dto.DurationFilter
import com.example.hybridconnect.presentation.dto.FilterChipState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

private const val TAG = "TransactionsViewModel"

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val observeTransactionsUseCase: ObserveTransactionsUseCase,
    private val retryTransactionUseCase: RetryTransactionUseCase,
) : ViewModel() {
    private val transactions: StateFlow<List<Transaction>> = observeTransactionsUseCase.transactions

    private val _filteredTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val filteredTransactions: StateFlow<List<Transaction>> = _filteredTransactions

    private val _selectedChip = MutableStateFlow<FilterChipState>(FilterChipState.All)
    val selectedChip: StateFlow<FilterChipState> = _selectedChip

    private val _selectedDurationFilterChip = MutableStateFlow<DurationFilter?>(null)
    val selectedDurationFilterChip: StateFlow<DurationFilter?> = _selectedDurationFilterChip

    init {
        getAllTransactions()
        observeFilteredTransactions()
    }

    private fun getAllTransactions() {
        viewModelScope.launch {
            observeTransactionsUseCase()
        }
    }

    fun setSelectedChip(chip: FilterChipState) {
        _selectedChip.value = chip
        if(chip == FilterChipState.Scheduled(0)){
            _selectedDurationFilterChip.value = null
        }
    }

    fun setSelectedDurationFilterChip(chip: DurationFilter?) {
        // exclude Scheduled from Duration Filtering
        if(_selectedChip.value != FilterChipState.Scheduled(0)){
            _selectedDurationFilterChip.value = chip
        }
    }

    private fun observeFilteredTransactions() {
        combine(
            transactions,
            selectedChip,
            selectedDurationFilterChip,
        ) { transactions, selectedChip, selectedDurationFilterChip ->
            val notAutoRenewTransactions =
                transactions.filter { it.rescheduleInfo?.parentTransactionId == null }
            val notSubscriptionTransactions =
                notAutoRenewTransactions.filter { it.type != TransactionType.SUBSCRIPTION_RENEWAL }
            val notProcessedTransactions =
                notSubscriptionTransactions.filter { it.status != TransactionStatus.SCHEDULED }
            val durationFilteredTransactions =
                filterTransactionDuration(notProcessedTransactions, selectedDurationFilterChip)
            filterTransactionStatus(selectedChip, durationFilteredTransactions)
        }.onEach {
            _filteredTransactions.value = it
        }.launchIn(viewModelScope)
    }


    private fun filterTransactionStatus(
        chip: FilterChipState,
        transactions: List<Transaction>,
    ): List<Transaction> {
        return when (chip) {
            is FilterChipState.All -> transactions
            is FilterChipState.Failed -> transactions.filter { it.status == TransactionStatus.FAILED }
            is FilterChipState.Successful -> transactions.filter { it.status == TransactionStatus.SUCCESS }
            is FilterChipState.Unmatched -> transactions.filter { it.status == TransactionStatus.UNMATCHED }
            is FilterChipState.Scheduled -> {
                transactions.filter { it.status == TransactionStatus.RESCHEDULED }
            }
            is FilterChipState.SiteLink -> transactions.filter { it.type == TransactionType.SITE_LINK }
        }
    }

    private fun filterTransactionDuration(
        transactions: List<Transaction>,
        chip: DurationFilter?,
    ): List<Transaction> {
        return when (chip) {
            null -> transactions
            is DurationFilter.Today -> transactions.filter { it.time in todayRange() }
            is DurationFilter.Yesterday -> transactions.filter { it.time in yesterdayRange() }
            is DurationFilter.Last7Days -> transactions.filter { it.time in last7DaysRange() }
            is DurationFilter.Last30Days -> transactions.filter { it.time in last30DaysRange() }
        }
    }

    private fun todayRange(): LongRange {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        return startOfDay..endOfDay
    }

    private fun yesterdayRange(): LongRange {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val startOfDay = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        return startOfDay..endOfDay
    }

    private fun last7DaysRange(): LongRange {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -7)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        val endOfDay = Calendar.getInstance().timeInMillis

        return startOfDay..endOfDay
    }

    private fun last30DaysRange(): LongRange {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -30)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        val endOfDay = Calendar.getInstance().timeInMillis

        return startOfDay..endOfDay
    }

    fun retryTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                retryTransactionUseCase(transaction)
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
            }
        }
    }
}