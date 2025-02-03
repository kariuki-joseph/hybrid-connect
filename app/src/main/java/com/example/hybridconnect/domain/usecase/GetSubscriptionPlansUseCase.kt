package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.model.SubscriptionPlan
import com.example.hybridconnect.domain.repository.SubscriptionPlanRepository
import javax.inject.Inject
private const val TAG = "GetSubscriptionPlansUseCase"
class GetSubscriptionPlansUseCase @Inject constructor(
    private val subscriptionPlanRepository: SubscriptionPlanRepository
) {
    suspend operator fun invoke(): List<SubscriptionPlan> {
        try {
            return subscriptionPlanRepository.getActivePlans()
        } catch (e: Exception){
            Log.e(TAG, e.message.toString())
            throw e
        }
    }
}