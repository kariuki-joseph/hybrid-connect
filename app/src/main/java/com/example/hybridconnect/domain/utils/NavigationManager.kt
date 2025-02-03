package com.example.hybridconnect.domain.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NavigationManager {
    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent

    fun to(route: String) {
        _navigationEvent.value = NavigationEvent.To(route)
    }

    fun back() {
        _navigationEvent.value = NavigationEvent.Back
    }

    fun resetNavigationEvent() {
        _navigationEvent.value = null
    }
}

sealed class NavigationEvent {
    data object Back : NavigationEvent()
    data class To(val route: String) : NavigationEvent()
}