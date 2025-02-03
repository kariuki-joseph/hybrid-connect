package com.example.hybridconnect.domain.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class UssdDialerWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return Result.success()
    }
}