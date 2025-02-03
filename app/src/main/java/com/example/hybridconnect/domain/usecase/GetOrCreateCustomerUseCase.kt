package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.repository.CustomerRepository
import com.example.hybridconnect.domain.utils.formatPhoneToTenDigits
import javax.inject.Inject

class GetOrCreateCustomerUseCase @Inject constructor(
    private val customerRepository: CustomerRepository,
) {
    suspend operator fun invoke(phone: String, name: String): Customer {
        val formattedPhone = formatPhoneToTenDigits(phone)

        if (customerRepository.isCustomerRegistered(formattedPhone)) {
            return customerRepository.getCustomerByPhone(formattedPhone)
        }
        val newCustomer = Customer(
            id = formattedPhone.hashCode(),
            name = name,
            phone = formattedPhone
        )
        customerRepository.insertCustomer(newCustomer)
        return newCustomer
    }

}