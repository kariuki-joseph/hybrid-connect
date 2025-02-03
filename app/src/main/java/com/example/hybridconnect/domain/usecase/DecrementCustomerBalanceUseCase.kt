package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.repository.CustomerRepository
import javax.inject.Inject

class DecrementCustomerBalanceUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customer: Customer, amount: Int) {
        customerRepository.decrementAccountBalance(customer, amount)
    }
}