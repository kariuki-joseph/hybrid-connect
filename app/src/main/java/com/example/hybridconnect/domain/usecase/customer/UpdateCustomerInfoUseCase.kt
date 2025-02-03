package com.example.hybridconnect.domain.usecase.customer

import android.util.Log
import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.repository.CustomerRepository
import javax.inject.Inject

private const val TAG = "UpdateCustomerInfoUseCase"

class UpdateCustomerInfoUseCase @Inject constructor(
    private val customerRepository: CustomerRepository,
) {
    suspend operator fun invoke(customer: Customer) {
        try {
            customerRepository.updateCustomerInfo(customer)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            e.printStackTrace()
            throw e
        }
    }
}