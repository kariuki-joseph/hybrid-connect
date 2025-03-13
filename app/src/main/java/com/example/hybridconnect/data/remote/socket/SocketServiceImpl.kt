package com.example.hybridconnect.data.remote.socket

import android.util.Log
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.enums.SocketEvent
import com.example.hybridconnect.domain.model.ConnectedApp
import com.example.hybridconnect.domain.repository.ConnectedAppRepository
import com.example.hybridconnect.domain.repository.SettingsRepository
import com.example.hybridconnect.domain.services.SocketService
import com.example.hybridconnect.domain.usecase.RetryUnforwardedTransactionsUseCase
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

private const val TAG = "SocketServiceImpl"

class SocketServiceImpl(
    private val serverUrl: String,
    private val settingsRepository: SettingsRepository,
    private val connectedAppRepository: ConnectedAppRepository,
    private val retryUnforwardedTransactionsUseCase: RetryUnforwardedTransactionsUseCase
) : SocketService {
    private lateinit var socket: Socket
    private val eventListeners = mutableMapOf<String, (List<Any>) -> Unit>()
    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    override fun connect() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Trying to connect to socket...")
                val agentId = settingsRepository.getSetting(AppSetting.AGENT_ID)
                if (agentId.isEmpty()) {
                    invokeListener(
                        SocketEvent.EVENT_CONNECT_ERROR.name,
                        listOf("You need to be logged in to use HybridConnect")
                    )
                    Log.d(TAG, "Agent not logged in. Could not connect")
                    throw Exception("Agent not logged in. Could not connect")
                }

                val options = IO.Options().apply {
                    auth = mapOf(
                        "userId" to agentId,
                    )
                }

                socket = IO.socket(serverUrl, options)

                socket.on(Socket.EVENT_CONNECT) {
                    Log.d(TAG, "Socket connected")
                    _isConnected.value = true
                }
                socket.on(Socket.EVENT_DISCONNECT) {
                    println("Socket disconnected")
                    _isConnected.value = false
                    markAllAppsOffline()
                }

                registerOnlineStatusListeners()
                registerDynamicEventListeners()
                socket.connect()
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
            }
        }
    }

    override fun disconnect() {
        if (::socket.isInitialized) {
            socket.disconnect()
        }
    }

    override fun sendMessageToApp(app: ConnectedApp, data: Any) {
        if (::socket.isInitialized) {
            val message = JSONObject()
            message.put("connectId", app.connectId)
            message.put("message", data)
            socket.emit(SocketEvent.EVENT_SEND_MESSAGE.name, message)
        }
    }

    private fun registerDynamicEventListeners() {
        eventListeners.forEach { (event, listener) ->
            socket.on(event) { args ->
                listener(args.toList())
            }
        }
    }

    override fun on(event: String, listener: (List<Any>) -> Unit) {
        if (!eventListeners.containsKey(event)) {
            Log.d(TAG, "Adding new socket event listener $event")
            eventListeners[event] = listener
            if (::socket.isInitialized && socket.connected()) {
                socket.on(event) { args ->
                    listener(args.toList())
                }
            } else {
                println("Socket is not connected yet. Event $event will be registered once connected.")
            }
        }
    }

    override fun off(event: String) {
        eventListeners.remove(event)
    }

    private fun invokeListener(event: String, data: List<Any>) {
        val listener = eventListeners[event]
        listener?.invoke(data)
    }

    private fun registerOnlineStatusListeners() {
        socket.on(SocketEvent.EVENT_APP_CONNECTED.name) { args ->
            val connectId = args.getOrNull(0) as? String ?: return@on
            onAppConnectedCallBack(connectId)
        }

        socket.on(SocketEvent.EVENT_APP_DISCONNECTED.name) { args ->
            val connectId = args.getOrNull(0) as? String ?: return@on
            CoroutineScope(Dispatchers.IO).launch {
                connectedAppRepository.updateOnlineStatus(connectId, false)
            }
        }
    }

    private fun markAllAppsOffline() {
        CoroutineScope(Dispatchers.IO).launch {
            connectedAppRepository.markAllAppsOffline()
            Log.d(TAG, "All connected apps marked as offline")
        }
    }

    private fun onAppConnectedCallBack(connectId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            connectedAppRepository.updateOnlineStatus(connectId, true)
            retryUnforwardedTransactionsUseCase()
        }
    }
}