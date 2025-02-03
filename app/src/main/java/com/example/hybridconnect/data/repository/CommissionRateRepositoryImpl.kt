package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.local.dao.CommissionRateDao
import com.example.hybridconnect.data.mappers.toEntity
import com.example.hybridconnect.data.mappers.toDomain
import com.example.hybridconnect.data.remote.api.ApiService
import com.example.hybridconnect.domain.model.CommissionRate
import com.example.hybridconnect.domain.repository.CommissionRateRepository

private const val TAG = "CommissionRatRepositoryImpl"

class CommissionRateRepositoryImpl(
    private val commissionRateDao: CommissionRateDao,
    private val apiService: ApiService,
) : CommissionRateRepository {
    override suspend fun refreshCommissionRates() {
        try {
            val response = apiService.getCommissionRates()
            if (!response.isSuccessful) {
                throw Exception("Error getting commission rates")
            }
            if (response.body() != null && response.body()?.success == false) {
                throw Exception(response.body()?.msg)
            }

            response.body()?.data?.let { rates ->
                val dbRates = commissionRateDao.getAllRates()

                val ratesToDelete = dbRates.filter { dbRate ->
                    !rates.any { apiRate -> apiRate.toEntity().id == dbRate.id }
                }

                ratesToDelete.forEach { dbRate ->
                    commissionRateDao.deleteCommissionRate(dbRate)
                    Log.d(TAG, "Deleted commission rate with ID: ${dbRate.id}")
                }

                rates.forEach { apiRate ->
                    val dbRate = commissionRateDao.getRateById(apiRate.toEntity().id)
                    if (dbRate == null) {
                        commissionRateDao.insertCommissionRate(apiRate.toEntity())
                        return
                    }
                    if (shouldUpdateRates(apiRate.toEntity().updatedAt, dbRate.updatedAt)) {
                        commissionRateDao.updateCommissionRate(apiRate.toEntity())
                        Log.d(TAG, "Updated commission rate with ID: ${apiRate.id}")
                    }
                }
            }

        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            throw e
        }
    }

    override suspend fun getCommissionRates(): List<CommissionRate> {
        return commissionRateDao.getAllRates().map { it.toDomain() }
    }

    override suspend fun getCommissionRateForAmount(amount: Int): Double {
        return commissionRateDao.getRateForAmount(amount) ?: 0.0
    }

    private fun shouldUpdateRates(apiUpdatedAt: Long, dbUpdatedAt: Long): Boolean {
        return apiUpdatedAt > dbUpdatedAt
    }

}