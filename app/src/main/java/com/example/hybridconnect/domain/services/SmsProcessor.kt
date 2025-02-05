package com.example.hybridconnect.domain.services

import android.util.Log
import com.example.hybridconnect.domain.exception.InvalidMessageFormatException
import com.example.hybridconnect.domain.exception.RecommendationTimedOutException
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.usecase.ExtractMessageDetailsUseCase
import com.example.hybridconnect.domain.usecase.ForwardMessagesUseCase
import com.example.hybridconnect.domain.usecase.ValidateMessageUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SmsProcessor"

class SmsProcessor @Inject constructor(
    private val validateMessageUseCase: ValidateMessageUseCase,
    private val extractMessageDetailsUseCase: ExtractMessageDetailsUseCase,
    private val transactionRepository: TransactionRepository,
    private val forwardMessagesUseCase: ForwardMessagesUseCase,
) {
    fun processMessage(message: String, sender: String, simSlot: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                validateMessageUseCase(message, sender, simSlot)
                val sms = extractMessageDetailsUseCase(message)
                val transaction = transactionRepository.createFromMessage(sms.message)
                forwardMessagesUseCase(transaction)
            } catch (e: RecommendationTimedOutException) {
                Log.e(TAG, e.message, e)
                processRecommendationTimeoutMessage(e.msg)
            } catch (e: InvalidMessageFormatException) {
                println("Invalid message format: ${e.message}")
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    private fun processRecommendationTimeoutMessage(message: String) {
        val transaction = transactionRepository.createFromMessage(message)
        forwardMessagesUseCase(transaction)
    }
}