package com.example.hybridconnect.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.enums.LoadingState
import com.example.hybridconnect.domain.model.RawSmsMessage
import com.example.hybridconnect.domain.services.SmsProcessor
import com.example.hybridconnect.domain.usecase.ReadMpesaMessagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ForwardMessagesViewModel"

@HiltViewModel
class ForwardMessagesViewModel @Inject constructor(
    private val readMpesaMessagesUseCase: ReadMpesaMessagesUseCase,
    private val smsProcessor: SmsProcessor,
) : ViewModel() {

    private val _mpesaMessages = MutableStateFlow<List<RawSmsMessage>>(emptyList())
    val mpesaMessages: StateFlow<List<RawSmsMessage>> = _mpesaMessages

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    fun loadMpesaMessages(count: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadingState.value = LoadingState.Loading
            val messages = readMpesaMessagesUseCase(count)
            _mpesaMessages.value = messages
            _loadingState.value = LoadingState.Success("Messages loaded successfully")
        }
    }

    fun sendMessage(message: RawSmsMessage) {
        Log.d(TAG, "Trying to send message: $message")
        smsProcessor.processMessage(message.message, message.sender, message.simSlot)
    }

    fun removeMessage(message: RawSmsMessage) {
        _mpesaMessages.value = _mpesaMessages.value.toMutableList().apply {
            remove(message)
        }
    }
}