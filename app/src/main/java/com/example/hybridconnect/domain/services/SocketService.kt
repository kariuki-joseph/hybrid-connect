package com.example.hybridconnect.domain.services

import kotlinx.coroutines.flow.StateFlow

interface SocketService {
    val isConnected: StateFlow<Boolean>
    fun connect()
    fun disconnect()
    fun sendMessage(event: String, data: Any)
    fun on(event: String, listener: (List<Any>) -> Unit)
    fun off(event: String)
}