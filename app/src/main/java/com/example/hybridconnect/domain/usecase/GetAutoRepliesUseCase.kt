package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.model.AutoReply
import com.example.hybridconnect.domain.repository.AutoReplyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetAutoRepliesUseCase @Inject constructor(
    private val autoReplyRepository: AutoReplyRepository
) {
    private val _autoReplies = MutableStateFlow<List<AutoReply>>(emptyList())
    val autoReplies: StateFlow<List<AutoReply>> get() = _autoReplies

    suspend operator fun invoke(): StateFlow<List<AutoReply>>{
        _autoReplies.value = autoReplyRepository.getAutoReplies()
        return autoReplies
    }
}