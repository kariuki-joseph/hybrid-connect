package com.example.hybridconnect.domain.usecase

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.hybridconnect.domain.model.SubscriptionPackage
import com.example.hybridconnect.domain.repository.SubscriptionPlanRepository
import com.example.hybridconnect.domain.workers.SubscriptionUpdateWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "AddSubscriptionUseCase"

class AddSubscriptionUseCase @Inject constructor(
    private val subscriptionPlanRepository: SubscriptionPlanRepository,
    private val context: Context,
) {
    suspend operator fun invoke(subscriptionPackage: SubscriptionPackage) {
        try {
            subscriptionPlanRepository.addSubscription(subscriptionPackage)
            subscriptionPlanRepository.postSubscriptionPayment(subscriptionPackage)
            startSubscriptionSyncWorker()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }

    private fun startSubscriptionSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<SubscriptionUpdateWorker>(30, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag("SubscriptionUpdateWorker")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "SubscriptionUpdateWorker",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
        Log.d(TAG, "Subscription update work scheduled")
    }
}