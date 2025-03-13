package com.example.hybridconnect.domain.services

import android.util.Log
import com.example.hybridconnect.domain.exception.InvalidMessageFormatException
import com.example.hybridconnect.domain.exception.RecommendationTimedOutException
import com.example.hybridconnect.domain.usecase.CreateTransactionUseCase
import com.example.hybridconnect.domain.usecase.ExtractMessageDetailsUseCase
import com.example.hybridconnect.domain.usecase.ForwardTransactionUseCase
import com.example.hybridconnect.domain.usecase.GetOfferByPriceUseCase
import com.example.hybridconnect.domain.usecase.ValidateMessageUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SmsProcessor"

class SmsProcessor @Inject constructor(
    private val validateMessageUseCase: ValidateMessageUseCase,
    private val extractMessageDetailsUseCase: ExtractMessageDetailsUseCase,
    private val getOfferByPriceUseCase: GetOfferByPriceUseCase,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val forwardTransactionUseCase: ForwardTransactionUseCase,
) {
    fun processMessage(message: String, sender: String, simSlot: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "Processing message.. ${message.take(15)}")
            try {
                validateMessageUseCase(message, sender, simSlot)
                val sms = extractMessageDetailsUseCase(message)
                val offer = getOfferByPriceUseCase(sms.amount)
                val transaction = createTransactionUseCase(sms, offer)
                forwardTransactionUseCase(transaction)
            } catch (e: RecommendationTimedOutException) {
                Log.e(TAG, e.message, e)
            } catch (e: InvalidMessageFormatException) {
                println("Invalid message format: ${e.message}")
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }
}