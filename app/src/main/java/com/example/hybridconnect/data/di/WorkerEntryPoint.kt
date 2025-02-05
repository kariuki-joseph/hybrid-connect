package com.example.hybridconnect.data.di

import com.example.hybridconnect.domain.repository.ConnectedAppRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.services.SocketService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WorkerEntryPoint {
    fun transactionRepository(): TransactionRepository
    fun socketService(): SocketService
    fun connectedAppRepository(): ConnectedAppRepository
}