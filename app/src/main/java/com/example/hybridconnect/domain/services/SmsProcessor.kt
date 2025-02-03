package com.example.hybridconnect.domain.services

import android.util.Log
import com.example.hybridconnect.domain.exception.InvalidMessageFormatException
import com.example.hybridconnect.domain.exception.RecommendationTimedOutException
import com.example.hybridconnect.domain.usecase.ExtractMessageDetailsUseCase
import com.example.hybridconnect.domain.usecase.ValidateMessageUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
private const val TAG = "SmsProcessor"
class SmsProcessor @Inject constructor(
    private val validateMessageUseCase: ValidateMessageUseCase,
    private val extractMessageDetailsUseCase: ExtractMessageDetailsUseCase,
) {
    fun processMessage(message: String, sender: String, simSlot: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                validateMessageUseCase(message, sender, simSlot)
                val sms = extractMessageDetailsUseCase(message)

            } catch (e: RecommendationTimedOutException){
                Log.e(TAG, e.message, e)
                processRecommendationTimeoutMessage(e.phoneNumber)
            }
            catch (e: InvalidMessageFormatException) {
                println("Invalid message format: ${e.message}")
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

   private fun processRecommendationTimeoutMessage(phoneNumber: String){

    }
}