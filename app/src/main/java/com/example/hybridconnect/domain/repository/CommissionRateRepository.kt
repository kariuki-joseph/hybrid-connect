package com.example.hybridconnect.domain.repository

import com.example.hybridconnect.domain.model.CommissionRate

interface CommissionRateRepository {
    suspend fun refreshCommissionRates()

    suspend fun getCommissionRates(): List<CommissionRate>

    suspend fun getCommissionRateForAmount(amount: Int): Double
}