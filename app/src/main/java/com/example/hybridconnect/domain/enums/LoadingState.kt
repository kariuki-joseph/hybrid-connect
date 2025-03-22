package com.example.hybridconnect.domain.enums

sealed class LoadingState {
    data object Idle : LoadingState()
    data object Loading : LoadingState()
    data class Success(val data: String) : LoadingState()
    data class Failure(val error: String) : LoadingState()
}