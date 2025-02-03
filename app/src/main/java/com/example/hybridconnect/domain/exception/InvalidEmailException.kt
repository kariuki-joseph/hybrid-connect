package com.example.hybridconnect.domain.exception

class InvalidEmailException(message: String): Exception(message) {
    companion object {
        private const val TAG = "InvalidEmailException"
    }
}