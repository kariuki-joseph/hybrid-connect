package com.example.hybridconnect.domain.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hybridconnect.data.di.WorkerEntryPoint
import com.example.hybridconnect.domain.repository.SubscriptionPackageRepository
import com.example.hybridconnect.domain.repository.SubscriptionPlanRepository
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import retrofit2.HttpException

@HiltWorker
class SubscriptionUpdateWorker @AssistedInject constructor(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    private val subscriptionPlanRepository: SubscriptionPlanRepository
    private val subscriptionPackageRepository: SubscriptionPackageRepository

    init {
        val entryPoint = EntryPointAccessors.fromApplication(context, WorkerEntryPoint::class.java)
        subscriptionPlanRepository = entryPoint.subscriptionPlanRepository()
        subscriptionPackageRepository = entryPoint.subscriptionPackageRepository()
    }

    override suspend fun doWork(): Result {
        return try {
            subscriptionPlanRepository.syncActiveSubscriptions()
            Result.success()
        } catch (e: HttpException) {
            Log.e(TAG, "Network error: ${e.message()}")
            Result.retry()
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "SubscriptionUpdateWorker"
    }
}