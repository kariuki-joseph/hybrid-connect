package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.local.dao.PrefsDao
import com.example.hybridconnect.data.local.dao.SubscriptionPackageDao
import com.example.hybridconnect.data.local.entity.PrefEntity
import com.example.hybridconnect.data.local.entity.SubscriptionPackageEntity
import com.example.hybridconnect.data.mappers.toApiError
import com.example.hybridconnect.data.mappers.toDomain
import com.example.hybridconnect.data.mappers.toEntity
import com.example.hybridconnect.data.remote.api.ApiService
import com.example.hybridconnect.domain.model.SubscriptionPackage
import com.example.hybridconnect.domain.repository.SubscriptionPackageRepository
import com.example.hybridconnect.domain.utils.Constants
import java.util.UUID

private const val TAG = "SubscriptionRepositoryImpl"

class SubscriptionPackageRepositoryImpl(
    private val subscriptionPackageDao: SubscriptionPackageDao,
    private val apiService: ApiService,
    private val prefsDao: PrefsDao,
) : SubscriptionPackageRepository {
    override suspend fun getSubscriptions(): List<SubscriptionPackage> {
        try {
            val response = apiService.getSubscriptions()
            if (!response.isSuccessful) {
                val errorMessage = response.errorBody().toApiError()?.message
                throw Exception(errorMessage ?: "Error fetching subscription packages")
            }
            if (response.body() == null) {
                throw Exception("Empty body for subscriptions received. Please try again")
            }
            val subscriptions: List<SubscriptionPackageEntity>? =
                response.body()?.data?.map { it.toEntity() }
            subscriptionPackageDao.deleteAllSubscriptionPackages()

            subscriptions?.forEach { sub ->
                subscriptionPackageDao.insert(sub)
            }

            return subscriptionPackageDao.getAllSubscriptionPackages().map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }

    override suspend fun getSubscription(subscriptionId: UUID): SubscriptionPackage? {
        try {
            return subscriptionPackageDao.getSubscriptionById(subscriptionId)?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }
}
