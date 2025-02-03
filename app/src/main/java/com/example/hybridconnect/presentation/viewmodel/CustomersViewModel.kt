package com.example.hybridconnect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.usecase.GetCustomersUseCase
import com.example.hybridconnect.domain.usecase.GetTransactionUseCase
import com.example.hybridconnect.domain.usecase.ObserveTransactionsUseCase
import com.example.hybridconnect.domain.usecase.customer.DeleteCustomerUseCase
import com.example.hybridconnect.domain.usecase.customer.UpdateCustomerInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val getCustomersUseCase: GetCustomersUseCase,
    private val updateCustomerInfoUseCase: UpdateCustomerInfoUseCase,
    private val deleteCustomerUseCase: DeleteCustomerUseCase,
    private val observeTransactionsUseCase: ObserveTransactionsUseCase
) : ViewModel() {
    val customers: StateFlow<List<Customer>> = getCustomersUseCase.customer

    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    private val selectedCustomer: StateFlow<Customer?> = _selectedCustomer

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage


    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName

    private val _accountBalance = MutableStateFlow("")
    val accountBalance: StateFlow<String> = _accountBalance

    private val _editSuccess = MutableStateFlow(false)
    val editSuccess: StateFlow<Boolean> = _editSuccess

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess

    init {
        fetchCustomers()
    }

    private fun fetchCustomers() {
        viewModelScope.launch(Dispatchers.IO) {
            getCustomersUseCase()
        }
    }

    fun getCustomerByPhone(customerPhone: String) {
        _selectedCustomer.value = customers.value.find { it.phone == customerPhone }
        _customerName.value = selectedCustomer.value?.name ?: ""
        _accountBalance.value = selectedCustomer.value?.accountBalance?.toString() ?: ""
    }

    fun onCustomerNameChanged(newName: String) {
        _customerName.value = newName
    }

    fun onAccountBalanceChanged(newBalance: String) {
        _accountBalance.value = newBalance
    }

    fun updateCustomer() {
        val customer = _selectedCustomer.value
        if (customer == null) {
            _errorMessage.value = "You have not selected any customer"
            return
        }

        if (_customerName.value.length < 3) {
            _errorMessage.value = "Customer name should be at least 3 characters"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedCustomer = customer.copy(
                    name = _customerName.value,
                    accountBalance = _accountBalance.value.toIntOrNull() ?: 0
                )

                _isLoading.value = true
                updateCustomerInfoUseCase(updatedCustomer)
                fetchCustomers()
                _selectedCustomer.value = null
                _editSuccess.value = true
                _successMessage.value = "Customer updated successfully"
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCustomer() {
        val customer = _selectedCustomer.value
        if (customer == null) {
            _errorMessage.value = "You need to select a customer first"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                deleteCustomerUseCase(customer)
                fetchCustomers()
                observeTransactionsUseCase()

                _selectedCustomer.value = null
                _deleteSuccess.value = true
                _successMessage.value = "Customer deleted successfully"
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSnackbarMessage() {
        _successMessage.value =  null
    }

    fun resetEditSuccess() {
        _editSuccess.value = false
    }

    fun resetDeleteSuccess() {
        _deleteSuccess.value = false
    }
}