package com.example.hybridconnect.domain.usecase.subscriptions

import com.example.hybridconnect.domain.model.SubscriptionPackage
import com.example.hybridconnect.domain.repository.SubscriptionPackageRepository
import java.util.UUID
import javax.inject.Inject

class GetSubscriptionPackageUseCase @Inject constructor(
    private val subscriptionPackageRepository: SubscriptionPackageRepository,
) {
    suspend operator fun invoke(subscriptionId: UUID): SubscriptionPackage? {
        return subscriptionPackageRepository.getSubscription(subscriptionId)
    }
}