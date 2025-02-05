package com.example.hybridconnect.domain.usecase.customer

import android.util.Log
import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.repository.CustomerRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import javax.inject.Inject

private const val TAG = "DeleteCustomerUseCase"
class DeleteCustomerUseCase @Inject constructor (
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(customer: Customer){
        try {
            customerRepository.deleteCustomer(customer)
        }catch(e: Exception){
            Log.e(TAG, e.message.toString())
            e.printStackTrace()
            throw e
        }
    }
}