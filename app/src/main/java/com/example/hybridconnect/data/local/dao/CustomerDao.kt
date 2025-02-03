package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hybridconnect.data.local.entity.CustomerEntity

@Dao
interface CustomerDao {
    @Insert
    suspend fun insert(customer: CustomerEntity)

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Int): CustomerEntity

    @Query("SELECT * FROM customers WHERE accountBalance != 0 ORDER BY lastPurchaseTime DESC")
    suspend fun getAllCustomers(): List<CustomerEntity>

    @Query("SELECT * FROM customers WHERE phone = :phone")
    suspend fun getCustomerByPhone(phone: String): CustomerEntity

    @Query("SELECT COUNT(*) AS customer_count FROM customers WHERE phone= :phone")
    suspend fun getCustomerCount(phone: String): Int

    @Query("UPDATE customers SET accountBalance = :newBalance WHERE id = :id")
    suspend fun updateCustomerBalance(id: Int, newBalance: Int)

    @Query("UPDATE customers SET lastPurchaseTime = :lastPurchaseTime WHERE id = :id")
    suspend fun updateLastPurchaseTime(id: Int, lastPurchaseTime: Long)

    @Update
    suspend fun updateCustomerInfo(customer: CustomerEntity)

    @Delete
    suspend fun deleteCustomer(customer: CustomerEntity)
}
