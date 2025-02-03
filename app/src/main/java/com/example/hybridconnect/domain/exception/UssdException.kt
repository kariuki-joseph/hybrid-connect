package com.example.hybridconnect.domain.exception

class UssdException(message: String) : Exception(message) {
    companion object {
        private const val TAG = "UssdException"
    }
}