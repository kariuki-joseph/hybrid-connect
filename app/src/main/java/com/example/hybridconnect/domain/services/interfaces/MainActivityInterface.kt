package com.example.hybridconnect.domain.services.interfaces

interface MainActivityInterface {
    fun areWeTheDefaultMessagingApp(): Boolean
    fun requestDefaultSmsAppSelection();
}