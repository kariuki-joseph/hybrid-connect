package com.example.hybridconnect.domain.usecase

import android.util.Log
import java.util.UUID
import javax.inject.Inject

private const val TAG = "DeleteTransactionUseCase"

class DeleteTransactionUseCase @Inject constructor() {
    suspend operator fun invoke(transactionId: UUID) {
        try {

        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }
}