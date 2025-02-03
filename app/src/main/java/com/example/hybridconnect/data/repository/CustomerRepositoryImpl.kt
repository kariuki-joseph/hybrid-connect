package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.local.dao.CustomerDao
import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.data.mappers.toDomain
import com.example.hybridconnect.data.mappers.toEntity
import com.example.hybridconnect.domain.repository.CustomerRepository

private const val TAG = "CustomerRepositoryImpl"
class CustomerRepositoryImpl(private val customerDao: CustomerDao) : CustomerRepository {
    override suspend fun insertCustomer(customer: Customer) {
        customerDao.insert(customer.toEntity())
    }

    override suspend fun getAllCustomers(): List<Customer> {
        return customerDao.getAllCustomers().map { it.toDomain() }
    }

    override suspend fun getCustomerById(id: Int): Customer {
        return customerDao.getCustomerById(id).toDomain()
    }

    override suspend fun getCustomerByPhone(phone: String): Customer {
        return customerDao.getCustomerByPhone(phone).toDomain()
    }

    override suspend fun isCustomerRegistered(phone: String): Boolean {
        return customerDao.getCustomerCount(phone) > 0
    }

    override suspend fun incrementAccountBalance(customer: Customer, amount: Int) {
        val existingBalance = customerDao.getCustomerById(customer.id).accountBalance
        customerDao.updateCustomerBalance(customer.id, (existingBalance + amount))
    }

    override suspend fun decrementAccountBalance(customer: Customer, amount: Int) {
        val existingBalance = customerDao.getCustomerById(customer.id).accountBalance
        customerDao.updateCustomerBalance(customer.id, (existingBalance - amount))
    }

    override suspend fun updateLastPurchaseTime(customer: Customer, lastPurchaseTime: Long) {
        customerDao.updateLastPurchaseTime(customer.id, lastPurchaseTime)
    }

    override suspend fun updateCustomerInfo(customer: Customer) {
        try {
            customerDao.updateCustomerInfo(customer.toEntity())
        } catch(e: Exception){
            Log.e(TAG, e.message.toString())
            throw e
        }
    }

    override suspend fun deleteCustomer(customer: Customer) {
        try{
            customerDao.deleteCustomer(customer.toEntity())
        } catch(e: Exception){
            Log.e(TAG, e.message.toString())
            throw e
        }
    }
}