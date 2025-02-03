package com.example.hybridconnect.domain.usecase

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.hybridconnect.domain.enums.SubscriptionType
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.enums.TransactionType
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.model.isExpired
import com.example.hybridconnect.domain.repository.SubscriptionPlanRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.workers.UssdDialerWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val TAG = "DialUssdUseCase"

class DialUssdUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionHandler: PermissionHandlerUseCase,
    private val transactionRepository: TransactionRepository,
    private val subscriptionPlanRepository: SubscriptionPlanRepository,
    private val updateTransactionStatusUseCase: UpdateTransactionStatusUseCase,
    private val updateTransactionResponseUseCase: UpdateTransactionResponseUseCase,
) {
    suspend operator fun invoke(
        transaction: Transaction,
        time: Long = System.currentTimeMillis(),
    ) {
        if (!permissionHandler.hasNecessaryPermissions()) {
            permissionHandler.requestNecessaryPermissions(context as Activity)
            return
        }

        if (!subscriptionPlanRepository.hasActivePlan() && transaction.type != TransactionType.SUBSCRIPTION_RENEWAL) {
            showSubscriptionPlanNotActiveError(transaction)
            return
        }
        checkIfShouldUpdateTokens(transaction)
        try {
            transactionRepository.transactionQueue.add(transaction)

            Log.d(TAG, "Current transaction queue size: ${transactionRepository.transactionQueue.size}")

            val delay = time - System.currentTimeMillis()

            val ussdWork = OneTimeWorkRequestBuilder<UssdDialerWorker>().build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork("UssdDialWork", ExistingWorkPolicy.KEEP, ussdWork)
        } catch (e: Exception) {
            Log.d(TAG, e.message, e)
        }
    }

    private suspend fun showSubscriptionPlanNotActiveError(transaction: Transaction) {
        updateTransactionResponseUseCase(
            transaction.id,
            "Failed. You have no active subscription. Please subscribe then retry"
        )
        updateTransactionStatusUseCase(transaction.id, TransactionStatus.FAILED)
    }

    private suspend fun checkIfShouldUpdateTokens(transaction: Transaction) {
        val activePlans = subscriptionPlanRepository.getActivePlans()
        val isSubscriptionRenewal = transaction.type == TransactionType.SUBSCRIPTION_RENEWAL

        val hasActiveUnlimitedPlan = activePlans.any { plan ->
            plan.type == SubscriptionType.UNLIMITED && !plan.isExpired()
        }

        if (!hasActiveUnlimitedPlan && !isSubscriptionRenewal) {
            val hasValidLimitedPlan = activePlans.any { plan ->
                plan.type == SubscriptionType.LIMITED && !plan.isExpired()
            }

            if (hasValidLimitedPlan) {
                subscriptionPlanRepository.decrementTokens(1)
                Log.d(
                    TAG,
                    "Decremented one token from a limited plan because there is no active unlimited plan."
                )
            } else {
                Log.d(TAG, "No valid LIMITED plans available to decrement tokens from.")
            }
        } else {
            Log.d(
                TAG,
                "Conditions not met for decrementing tokens: hasActiveUnlimitedPlan=$hasActiveUnlimitedPlan, isSubscriptionRenewal=$isSubscriptionRenewal"
            )
        }
    }
}