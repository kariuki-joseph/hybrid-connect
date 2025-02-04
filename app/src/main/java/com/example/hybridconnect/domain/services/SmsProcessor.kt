package com.example.hybridconnect.domain.services

import android.util.Log
import com.example.hybridconnect.domain.enums.SocketEvent
import com.example.hybridconnect.domain.exception.InvalidMessageFormatException
import com.example.hybridconnect.domain.exception.RecommendationTimedOutException
import com.example.hybridconnect.domain.repository.ConnectedAppRepository
import com.example.hybridconnect.domain.usecase.ExtractMessageDetailsUseCase
import com.example.hybridconnect.domain.usecase.ValidateMessageUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

private const val TAG = "SmsProcessor"

class SmsProcessor @Inject constructor(
    private val validateMessageUseCase: ValidateMessageUseCase,
    private val extractMessageDetailsUseCase: ExtractMessageDetailsUseCase,
    private val connectedAppRepository: ConnectedAppRepository,
    private val socketService: SocketService,
) {
    private var lastAssignedIndex = -1

    fun processMessage(message: String, sender: String, simSlot: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                validateMessageUseCase(message, sender, simSlot)
                val sms = extractMessageDetailsUseCase(message)
                sendWebSocketMessage(sms.message)
            } catch (e: RecommendationTimedOutException) {
                Log.e(TAG, e.message, e)
                processRecommendationTimeoutMessage(e.phoneNumber)
            } catch (e: InvalidMessageFormatException) {
                println("Invalid message format: ${e.message}")
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    private fun processRecommendationTimeoutMessage(phoneNumber: String) {

    }

    private fun sendWebSocketMessage(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            connectedAppRepository.getConnectedApps().collect { apps ->
                val activeApps = apps.filter { it.isOnline }

                if(activeApps.isEmpty()){
                    Log.e(TAG, "No connected apps available to process the message")
                    return@collect
                }

                // move to the next app in a round-robin order
                lastAssignedIndex = (lastAssignedIndex +1 ) % activeApps.size
                val selectedApp = activeApps[lastAssignedIndex]

                Log.d(TAG, "Sending message to ${selectedApp.connectId}")
                socketService.sendMessageToApp(selectedApp, message)
                connectedAppRepository.incrementMessagesSent(selectedApp)
            }
        }
    }
}