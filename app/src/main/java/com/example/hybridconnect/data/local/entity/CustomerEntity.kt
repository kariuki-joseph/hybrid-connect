package com.example.hybridconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "customers",
    indices = [
        androidx.room.Index(value = ["phone"])
    ]
)
data class CustomerEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val phone: String,
    val accountBalance: Int,
    val lastPurchaseTime: Long
)