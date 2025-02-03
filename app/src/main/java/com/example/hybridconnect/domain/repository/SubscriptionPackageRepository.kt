package com.example.hybridconnect.domain.repository

import com.example.hybridconnect.domain.model.SubscriptionPackage
import java.util.UUID

interface SubscriptionPackageRepository {
    suspend fun getSubscriptions(): List<SubscriptionPackage>
    suspend fun getSubscription(subscriptionId: UUID): SubscriptionPackage?
}