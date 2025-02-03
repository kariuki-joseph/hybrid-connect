package com.example.hybridconnect.domain.repository

interface HybridConnectRepository {
    suspend fun generateConnectId(): String
}