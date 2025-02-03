package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.local.dao.SubscriptionPlanDao
import com.example.hybridconnect.data.local.entity.SubscriptionPlanEntity
import com.example.hybridconnect.data.mappers.toApiError
import com.example.hybridconnect.data.mappers.toDomain
import com.example.hybridconnect.data.remote.api.ApiService
import com.example.hybridconnect.data.remote.api.request.PostSubscriptionPaymentRequest
import com.example.hybridconnect.data.remote.api.request.SyncSubscriptionRequest
import com.example.hybridconnect.domain.enums.SubscriptionType
import com.example.hybridconnect.domain.model.SubscriptionPackage
import com.example.hybridconnect.domain.model.SubscriptionPlan
import com.example.hybridconnect.domain.model.isExpired
import com.example.hybridconnect.domain.repository.SubscriptionPlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

private const val TAG = "SubscriptionPlanRepositoryImpl"

class SubscriptionPlanRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val subscriptionPlanDao: SubscriptionPlanDao,
) : SubscriptionPlanRepository {
    private val _activePlansFlow = MutableStateFlow<List<SubscriptionPlan>>(emptyList())
    override val activePlansFlow: StateFlow<List<SubscriptionPlan>> = _activePlansFlow

    override suspend fun getActivePlans(): List<SubscriptionPlan> {
        try {
            val plans = subscriptionPlanDao.getAllPlans().map { it.toDomain() }
            _activePlansFlow.emit(plans)
            return plans
        } catch (e: Exception) {
            Log.e(TAG, "getActivePlans: ", e)
            throw e
        }
    }

    override suspend fun decrementTokens(tokens: Int) {
        try {
            val plan = subscriptionPlanDao.getByType(SubscriptionType.LIMITED.name)
            if (plan != null) {
                val updatedPlan = plan.copy(limit = plan.limit - tokens)
                subscriptionPlanDao.updatePlan(updatedPlan)

                val updatedPlans = subscriptionPlanDao.getAllPlans().map { it.toDomain() }
                _activePlansFlow.emit(updatedPlans)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            throw e
        }
    }

    override suspend fun hasActivePlan(): Boolean {
        return try {
            val plans = subscriptionPlanDao.getAllPlans().map { it.toDomain() }
            plans.any { !it.isExpired() }
        } catch (e: Exception) {
            Log.e(TAG, "hasActivePlan: ", e)
            false
        }
    }

    override suspend fun addSubscription(subscriptionPackage: SubscriptionPackage) {
        try {
            val plan = subscriptionPlanDao.getByType(subscriptionPackage.type.name)

            val convertedLimit = when (subscriptionPackage.type) {
                SubscriptionType.UNLIMITED -> {
                    subscriptionPackage.limit * 60 * 60 * 1000
                }

                else -> {
                    subscriptionPackage.limit
                }
            }

            if (plan == null) {
                val offSet =
                    if (subscriptionPackage.type == SubscriptionType.UNLIMITED) System.currentTimeMillis() else 0

                val subscriptionPlan = SubscriptionPlanEntity(
                    type = subscriptionPackage.type.name,
                    subscriptionPackageId = subscriptionPackage.id,
                    limit = offSet + convertedLimit.toLong()
                )

                subscriptionPlanDao.createPlan(subscriptionPlan)
            } else {
                val updatedPlan = plan.copy(
                    subscriptionPackageId = subscriptionPackage.id,
                    limit = plan.limit + convertedLimit.toLong()
                )
                subscriptionPlanDao.updatePlan(updatedPlan)
            }

            val updatedPlans = subscriptionPlanDao.getAllPlans().map { it.toDomain() }
            _activePlansFlow.emit(updatedPlans)
        } catch (e: Exception) {
            Log.e(TAG, "subscribeToPackage: ", e)
            throw e
        }
    }

    override suspend fun postSubscriptionPayment(subscriptionPackage: SubscriptionPackage) {
        try {
            val request =
                PostSubscriptionPaymentRequest(packageId = subscriptionPackage.id.toString())
            val response = apiService.postSubscriptionPayment(request)
            if (!response.isSuccessful) {
                val errorMessage = response.errorBody()?.toApiError()?.message
                    ?: "Unknown error updating package subscription"
                throw Exception(errorMessage)
            }
            Log.d(
                TAG,
                "Subscription payment posted successfully for: ${subscriptionPackage.name}"
            )
        } catch (e: Exception){
            Log.e(TAG, "Error while posting subscription payment: ", e)
            throw e
        }
    }

    override suspend fun syncActiveSubscriptions() {
        try {
            val currentPlans = subscriptionPlanDao.getAllPlans()
            currentPlans.forEach { plan ->
                val request = SyncSubscriptionRequest(
                    packageType = SubscriptionType.valueOf(plan.type),
                    limit = plan.limit,
                )
                val response = apiService.syncSubscriptionPlan(request)
                if (!response.isSuccessful) {
                    val errorMessage = response.errorBody()?.toApiError()?.message
                        ?: "Unknown error syncing subscription plan"
                    throw Exception(errorMessage)
                }
                Log.d(TAG, "Subscription plan synced successfully for: ${plan.type}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while syncing subscription plan: ", e)
            throw e
        }
    }


}