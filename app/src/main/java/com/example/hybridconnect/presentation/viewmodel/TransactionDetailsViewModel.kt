package com.example.hybridconnect.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.usecase.DeleteTransactionUseCase
import com.example.hybridconnect.domain.usecase.ObserveTransactionsUseCase
import com.example.hybridconnect.domain.usecase.RetryTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

private const val TAG = "TransactionDetailsViewModel"

@HiltViewModel
class TransactionDetailsViewModel @Inject constructor(
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val observeTransactionsUseCase: ObserveTransactionsUseCase,
    private val retryTransactionUseCase: RetryTransactionUseCase,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val transactions: StateFlow<List<Transaction>> = observeTransactionsUseCase.transactions

    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction: StateFlow<Transaction?> = _transaction.asStateFlow()

    val scheduledTransactions: StateFlow<List<Transaction>> =
        combine(transactions, transaction) { transactionsList, currentTransaction ->
            if (currentTransaction != null) {
                transactionsList.filter { it.rescheduleInfo?.parentTransactionId == currentTransaction.id }
            } else {
                emptyList()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _onDeleteSuccess = MutableStateFlow(false)
    val onDeleteSuccess: StateFlow<Boolean> = _onDeleteSuccess.asStateFlow()

    private val _isRetrying = MutableStateFlow(false)
    val isRetrying: StateFlow<Boolean> = _isRetrying.asStateFlow()

    init {
        getTransactions()
    }

    private fun getTransactions() {
        viewModelScope.launch {
            observeTransactionsUseCase()
        }
    }

    fun getTransaction(transactionId: UUID) {
        viewModelScope.launch {
            getTransactions()
            transactions.map { list -> list.find { it.id == transactionId } }
                .collect { currentTransaction ->
                    _transaction.value = currentTransaction
                }
        }
    }

    fun retryTransaction(transaction: Transaction) {
        if (transaction.offer == null) {
            _snackbarMessage.value = "Cannot retry. Offer is not available"
            return
        }

        viewModelScope.launch {
            try {
                _isRetrying.value = true
                retryTransactionUseCase(transaction)
                observeTransactionStatus(transaction.id) { success, responseMessage ->
                    _isRetrying.value = false
                    _snackbarMessage.value =
                        if (success) "Success: $responseMessage" else "Failed: $responseMessage"
                }
            } catch (e: Exception) {
                _snackbarMessage.value = e.message
                _isRetrying.value = false
            }
        }
    }

    private suspend fun observeTransactionStatus(
        transactionId: UUID,
        callback: (success: Boolean, response: String) -> Unit,
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
                        _isRetrying.value = false
                        callback(true, transaction.responseMessage)
                    }

                    TransactionStatus.FAILED -> {
                        isProcessed = true
                        _isRetrying.value = false
                        callback(false, transaction.responseMessage)
                    }

                    else -> {}
                }
            }
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            try {
                _transaction.value?.let { transaction ->
                    deleteTransactionUseCase(transaction.id)
                    withContext(Dispatchers.Main) {
                        WorkManager.getInstance(context)
                            .cancelAllWorkByTag(transaction.id.toString())
                    }
                    _onDeleteSuccess.value = true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting transaction", e)
                _snackbarMessage.value = e.message
            }
        }
    }

    fun resetSnackbarMessage() {
        _snackbarMessage.value = null
    }
}