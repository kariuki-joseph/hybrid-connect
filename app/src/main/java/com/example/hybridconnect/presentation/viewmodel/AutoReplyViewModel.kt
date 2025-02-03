package com.example.hybridconnect.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridconnect.domain.enums.AutoReplyType
import com.example.hybridconnect.domain.model.AutoReply
import com.example.hybridconnect.domain.repository.AutoReplyRepository
import com.example.hybridconnect.domain.usecase.GetAutoRepliesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AutoReplyViewModel @Inject constructor(
    private val autoReplyRepository: AutoReplyRepository,
    private val getAutoRepliesUseCase: GetAutoRepliesUseCase,
) : ViewModel() {
    val autoReplies: StateFlow<List<AutoReply>> = getAutoRepliesUseCase.autoReplies

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private var selectedAutoReply: AutoReply? = null
    private val _autoReplyMessage = MutableStateFlow("")
    val autoReplyMessage: StateFlow<String> = _autoReplyMessage.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    init {
        fetchAutoReplies()
    }

    private fun fetchAutoReplies() {
        viewModelScope.launch(Dispatchers.IO) {
            getAutoRepliesUseCase()
        }
    }

    fun getAutoReplyByType(autoReplyType: AutoReplyType) {
        viewModelScope.launch {
            try {
                selectedAutoReply = autoReplyRepository.getAutoReplyByType(autoReplyType)
                _autoReplyMessage.value = selectedAutoReply?.message ?: ""
            } catch (e: Exception) {
                _message.value = e.toString()
            }
        }
    }

    fun onAutoReplyMessageChanged(message: String) {
        _autoReplyMessage.value = message
    }

    fun updateReplyMessage() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedAutoReply = selectedAutoReply?.copy(
                    message = _autoReplyMessage.value
                ) ?: throw Exception("You need to select at least one instance of AutoReply")
                _updateSuccess.value = false
                autoReplyRepository.updateAutoReply(updatedAutoReply)
                // fetch updated auto-replies
                getAutoRepliesUseCase()
                _updateSuccess.value = true
            } catch (e: Exception) {
                _message.value = e.message.toString()
            }
        }
    }

    fun onAutoReplyStatusChanged(autoReply: AutoReply, status: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (status) {
                    autoReplyRepository.activateAutoReply(autoReply.type)
                } else {
                    autoReplyRepository.deactivateAutoReply(autoReply.type)
                }
                // load new auto-replies
                fetchAutoReplies()
            } catch (e: Exception) {
                _message.value = e.message.toString()
            }
        }
    }

    fun resetResponseMessage() {
        _message.value = null
    }

    fun resetSuccessStatus() {
        _updateSuccess.value = false
    }
}