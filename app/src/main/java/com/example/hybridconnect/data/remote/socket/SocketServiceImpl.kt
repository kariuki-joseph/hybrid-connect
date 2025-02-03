package com.example.hybridconnect.data.remote.socket

import android.util.Log
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.enums.SocketEvent
import com.example.hybridconnect.domain.repository.PrefsRepository
import com.example.hybridconnect.domain.services.SocketService
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "SocketServiceImpl"
class SocketServiceImpl(
    private val serverUrl: String,
    private val prefsRepository: PrefsRepository,
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
                val agentId = prefsRepository.getSetting(AppSetting.AGENT_ID)
                val connectId = prefsRepository.getSetting(AppSetting.APP_CONNECT_ID)
                if (agentId.isEmpty()) {
                    invokeListener(SocketEvent.EVENT_CONNECT_ERROR.name, listOf("You need to be logged in to use this feature"))
                    Log.d(TAG, "Agent not logged in. Could not connect")
                    throw Exception("Agent not logged in. Could not connect")
                }

                if (connectId.isEmpty()) {
                    invokeListener(SocketEvent.EVENT_CONNECT_ERROR.name, listOf("App cannot connect without a valid Connect ID"))
                    Log.d(TAG, "App Connect ID missing")
                    throw Exception("App cannot connect without a valid Connect ID")
                }

                val options = IO.Options().apply {
                    auth = mapOf(
                        "userId" to agentId,
                        "connectId" to connectId
                    )
                }

                Log.d(TAG, "Connecting to socket using params ${options.auth}")

                socket = IO.socket(serverUrl, options)

                socket.on(Socket.EVENT_CONNECT) {
                    Log.d(TAG, "Socket connected")
                    _isConnected.value = true
                }
                socket.on(Socket.EVENT_DISCONNECT) {
                    println("Socket disconnected")
                    _isConnected.value = false
                }
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

    override fun sendMessage(event: String, data: Any) {
        if (::socket.isInitialized) {
            socket.emit(event, data)
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
            Log.d(TAG,"Adding new socket event listener $event")
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

}