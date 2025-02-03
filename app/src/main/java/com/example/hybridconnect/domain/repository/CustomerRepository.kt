package com.example.hybridconnect.domain.repository

import com.example.hybridconnect.domain.model.Customer

interface CustomerRepository {

    suspend fun insertCustomer(customer: Customer)

    suspend fun getAllCustomers(): List<Customer>

    suspend fun getCustomerById(id: Int): Customer

    suspend fun getCustomerByPhone(phone: String): Customer

    suspend fun isCustomerRegistered(phone: String): Boolean

    suspend fun incrementAccountBalance(customer: Customer, amount: Int)

    suspend fun decrementAccountBalance(customer: Customer, amount: Int)

    suspend fun updateLastPurchaseTime(customer: Customer, lastPurchaseTime: Long)
    suspend fun updateCustomerInfo(customer: Customer)
    suspend fun deleteCustomer(customer: Customer)
}
