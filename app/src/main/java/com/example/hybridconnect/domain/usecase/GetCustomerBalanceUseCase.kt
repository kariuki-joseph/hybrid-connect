package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.repository.CustomerRepository
import javax.inject.Inject

class GetCustomerBalanceUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customer: Customer): Int {
        return customerRepository.getCustomerById(customer.id).accountBalance
    }
}