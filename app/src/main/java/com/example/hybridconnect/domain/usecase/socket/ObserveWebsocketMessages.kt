package com.example.hybridconnect.domain.usecase.socket

import com.example.hybridconnect.domain.services.SocketService

class ObserveWebsocketMessages(private val socketService: SocketService) {
    operator fun invoke(event: String, listener: (String) -> Unit) {
        socketService.on(event) { args ->
            if (args.isNotEmpty()) {
                listener(args[0].toString())
            }
        }
    }
}