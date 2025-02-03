package com.example.hybridconnect.domain.repository

import com.example.hybridconnect.domain.model.SubscriptionPackage

interface PaymentRepository {
    fun setSubscription(subscriptionPackage: SubscriptionPackage)
    fun getSubscription(): SubscriptionPackage?
    suspend fun getAdminSubscriptionNumber(): String
    suspend fun getAdminSiteLinkNumber(): String?
}