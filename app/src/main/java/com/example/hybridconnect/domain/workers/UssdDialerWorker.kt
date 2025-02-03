package com.example.hybridconnect.domain.workers

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hybridconnect.data.di.WorkerEntryPoint
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.enums.AutoReplyType
import com.example.hybridconnect.domain.enums.OfferType
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.PrefsRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.usecase.FormatUssdUseCase
import com.example.hybridconnect.domain.usecase.GetTransactionUseCase
import com.example.hybridconnect.domain.usecase.IncrementAgentCommissionUseCase
import com.example.hybridconnect.domain.usecase.SendAutoReplyMessageUseCase
import com.example.hybridconnect.domain.usecase.SubscriptionIdFetcherUseCase
import com.example.hybridconnect.domain.usecase.UpdateTransactionResponseUseCase
import com.example.hybridconnect.domain.usecase.UpdateTransactionStatusUseCase
import com.example.hybridconnect.domain.utils.CustomUssdResponseCallback
import com.example.hybridconnect.domain.utils.isAlreadyRecommendedResponse
import com.example.hybridconnect.domain.utils.isOutOfServiceResponse
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.PriorityBlockingQueue

private const val TAG = "UssdDialerWorker"

class UssdDialerWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    private var transactionRepository: TransactionRepository
    private var prefsRepository: PrefsRepository
    private var getTransactionUseCase: GetTransactionUseCase
    private val updateTransactionStatusUseCase: UpdateTransactionStatusUseCase
    private val updateTransactionResponseUseCase: UpdateTransactionResponseUseCase
    private val incrementAgentCommissionUseCase: IncrementAgentCommissionUseCase
    private val sendAutoReplyMessageUseCase: SendAutoReplyMessageUseCase
    private val transactionQueue: PriorityBlockingQueue<Transaction>
    private val subscriptionIdFetcher: SubscriptionIdFetcherUseCase
    private val formatUssdUseCase: FormatUssdUseCase

    private val mutex = Mutex()

    init {
        val entryPoint = EntryPointAccessors.fromApplication(context, WorkerEntryPoint::class.java)
        transactionRepository = entryPoint.transactionRepository()
        prefsRepository = entryPoint.prefsRepository()
        getTransactionUseCase = entryPoint.getTransactionUseCase()
        updateTransactionStatusUseCase = entryPoint.getUpdateTransactionStatusUseCase()
        updateTransactionResponseUseCase = entryPoint.getUpdateTransactionReposeUseCase()
        incrementAgentCommissionUseCase = entryPoint.incrementAgentCommissionUseCase()
        sendAutoReplyMessageUseCase = entryPoint.sendAutoReplyMessageUseCase()
        transactionQueue = entryPoint.transactionRepository().transactionQueue
        subscriptionIdFetcher = entryPoint.subscriptionIdFetcherUseCase()
        formatUssdUseCase = entryPoint.formatUssdUseCase()
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            mutex.withLock {
                while (transactionQueue.isNotEmpty()){
                    Log.d(TAG, "Processing transaction... Current queue size: ${transactionQueue.size}")
                    val transaction = transactionQueue.poll() ?: continue
                    processTransaction(transaction)
                }
                Result.success()
            }
        }
    }


    @SuppressLint("MissingPermission", "NewApi")
    private fun updatedUssdService(
        transaction: Transaction,
        ussdCode: String,
        subscriptionId: Int,
    ): CompletableFuture<Result> {
        val res = CompletableFuture<Result>()
        val tm = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simManager: TelephonyManager = tm.createForSubscriptionId(subscriptionId)
        val callback = CustomUssdResponseCallback(transaction,
            onSuccess = { trans, response ->
                onUssdSuccess(trans, response)
                res.complete(Result.success())
            },
            onFailure = { trans, failureCode, response ->
                CoroutineScope(Dispatchers.IO).launch {
                    val isOutOfServiceRetryEnabled =
                        prefsRepository.getSetting(AppSetting.AUTO_RETRY_SERVICE_UNAVAILABLE_RESPONSE)
                            .toBoolean()
                    if (isOutOfServiceResponse(response) && isOutOfServiceRetryEnabled) {
                        Log.d(TAG, "Offer Failed Out Of Service. Adding back to queue...")
                        transaction.retries++
                        transactionQueue.put(transaction)
                        res.complete(Result.success())
                        return@launch
                    }
                }

                if (!isAlreadyRecommendedResponse(response) && trans.offer?.type == OfferType.DATA) {
                    Log.d(TAG, "Data Offer failed, adding back to queue...")
                    transaction.retries++
                    transactionQueue.put(transaction)
                    res.complete(Result.success())
                } else {
                    onUssdFailure(trans, failureCode, response)
                    res.complete(Result.failure())
                }
            })

        simManager.sendUssdRequest(ussdCode, callback, Handler(Looper.getMainLooper()))
        return res
    }

    private fun onUssdSuccess(transaction: Transaction, response: String) {
        Log.d(TAG, "USSD Success with response : $response")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                updateTransactionResponseUseCase(transaction.id, response)
                updateTransactionStatusUseCase(transaction.id, TransactionStatus.SUCCESS)
                sendAutoReplyMessageUseCase(transaction, AutoReplyType.SUCCESS)
                transaction.offer?.let { offer ->
                    if (offer.type == OfferType.DATA) {
                        val currentDate = LocalDate
                            .now()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        incrementAgentCommissionUseCase(offer, currentDate)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
            }
        }
    }

    private fun onUssdFailure(transaction: Transaction, failureCode: Int, response: String) {
        Log.d(TAG, "USSD Failed with code: $failureCode  Response: $response")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val savedTransaction = getTransactionUseCase(transaction.id)
                if (savedTransaction.status == TransactionStatus.SUCCESS) {
                    return@launch
                }
                updateTransactionResponseUseCase(transaction.id, response)
                updateTransactionStatusUseCase(transaction.id, TransactionStatus.FAILED)
                if (isAlreadyRecommendedResponse(response)) {
                    sendAutoReplyMessageUseCase(transaction, AutoReplyType.ALREADY_RECOMMENDED)
                } else {
                    sendAutoReplyMessageUseCase(transaction, AutoReplyType.FAILED)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
            }

        }
    }

    private suspend fun processTransaction(transaction: Transaction){
        val subscriptionId = subscriptionIdFetcher.getUssdSubscriptionId()
        val maxRetryCount = 7
        val maxRetryCountSms = 2

        if (transaction.offer?.type == OfferType.SMS && transaction.retries >= maxRetryCountSms) {
            updateTransactionResponseUseCase(transaction.id, "Failed: Max retries of ${transaction.retries} reached")
            updateTransactionStatusUseCase(transaction.id, TransactionStatus.FAILED)
            transactionRepository.resetRetriesForTransaction(transaction.id)
            return
        }

        if (transaction.retries >= maxRetryCount) {
            updateTransactionResponseUseCase(transaction.id, "Failed: Max retries of ${transaction.retries} reached")
            updateTransactionStatusUseCase(transaction.id, TransactionStatus.FAILED)
            transactionRepository.resetRetriesForTransaction(transaction.id)
            return
        }

        transactionRepository.incrementRetriesForTransaction(transaction.id)

        Log.d(
            TAG,
            "Dialing USSD using params: " +
                    "ussd: ${transaction.offer?.ussdCode}, " +
                    "subscriptionId: $subscriptionId, " +
                    "offer: ${transaction.offer}"
        )

        val ussdCode = formatUssdUseCase(transaction.offer?.ussdCode ?: "", transaction.customer.phone)

        try {
            updatedUssdService(transaction, ussdCode, subscriptionId).await()
        } catch (throwable: Throwable) {
            Log.e(TAG, "USSD Error", throwable)
            updateTransactionStatusUseCase(transaction.id, TransactionStatus.FAILED)
        }
    }
}