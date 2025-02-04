package com.example.hybridconnect.domain.services

import com.example.hybridconnect.domain.model.ConnectedApp
import kotlinx.coroutines.flow.StateFlow

interface SocketService {
    val isConnected: StateFlow<Boolean>
    fun connect()
    fun disconnect()
    fun sendMessageToApp(app: ConnectedApp, data: Any)
    fun on(event: String, listener: (List<Any>) -> Unit)
    fun off(event: String)
}