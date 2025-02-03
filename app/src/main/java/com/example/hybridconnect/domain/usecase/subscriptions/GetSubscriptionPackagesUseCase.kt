package com.example.hybridconnect.domain.usecase.subscriptions

import com.example.hybridconnect.domain.model.SubscriptionPackage
import com.example.hybridconnect.domain.repository.SubscriptionPackageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetSubscriptionPackagesUseCase @Inject constructor(
    private val subscriptionPackageRepository: SubscriptionPackageRepository
) {
    private val _subscriptions = MutableStateFlow<List<SubscriptionPackage>>(emptyList())
    val subscriptions: StateFlow<List<SubscriptionPackage>> get() = _subscriptions

    suspend operator fun invoke(): StateFlow<List<SubscriptionPackage>>{
        val result = subscriptionPackageRepository.getSubscriptions()
        _subscriptions.value = result
        return subscriptions
    }
}