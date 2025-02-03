package com.example.hybridconnect.domain.repository

import com.example.hybridconnect.domain.model.SubscriptionPackage
import com.example.hybridconnect.domain.model.SubscriptionPlan
import kotlinx.coroutines.flow.StateFlow

interface SubscriptionPlanRepository {
    val activePlansFlow: StateFlow<List<SubscriptionPlan>>
    suspend fun addSubscription(subscriptionPackage: SubscriptionPackage)
    suspend fun postSubscriptionPayment(subscriptionPackage: SubscriptionPackage)
    suspend fun syncActiveSubscriptions()
    suspend fun getActivePlans(): List<SubscriptionPlan>
    suspend fun decrementTokens(tokens: Int)
    suspend fun hasActivePlan(): Boolean
}