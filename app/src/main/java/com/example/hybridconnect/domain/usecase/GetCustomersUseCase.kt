package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetCustomersUseCase @Inject constructor(
    private val customerRepository: CustomerRepository,
) {
    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customer: StateFlow<List<Customer>> = _customers

    suspend operator fun invoke() {
        _customers.value = customerRepository.getAllCustomers()
    }
}