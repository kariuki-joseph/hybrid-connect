package com.example.hybridconnect.data.di

import com.example.hybridconnect.domain.repository.PrefsRepository
import com.example.hybridconnect.domain.repository.SubscriptionPackageRepository
import com.example.hybridconnect.domain.repository.SubscriptionPlanRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.usecase.DecrementCustomerBalanceUseCase
import com.example.hybridconnect.domain.usecase.FormatUssdUseCase
import com.example.hybridconnect.domain.usecase.GetTransactionUseCase
import com.example.hybridconnect.domain.usecase.IncrementAgentCommissionUseCase
import com.example.hybridconnect.domain.usecase.IncrementCustomerBalanceUseCase
import com.example.hybridconnect.domain.usecase.SendAutoReplyMessageUseCase
import com.example.hybridconnect.domain.usecase.SubscriptionIdFetcherUseCase
import com.example.hybridconnect.domain.usecase.UpdateTransactionResponseUseCase
import com.example.hybridconnect.domain.usecase.UpdateTransactionStatusUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WorkerEntryPoint {
    fun transactionRepository(): TransactionRepository
    fun prefsRepository(): PrefsRepository
    fun incrementCustomerBalanceUseCase(): IncrementCustomerBalanceUseCase
    fun decrementCustomerBalanceUseCase(): DecrementCustomerBalanceUseCase
    fun getTransactionUseCase(): GetTransactionUseCase
    fun getUpdateTransactionStatusUseCase(): UpdateTransactionStatusUseCase
    fun getUpdateTransactionReposeUseCase(): UpdateTransactionResponseUseCase
    fun incrementAgentCommissionUseCase(): IncrementAgentCommissionUseCase
    fun sendAutoReplyMessageUseCase(): SendAutoReplyMessageUseCase
    fun subscriptionPlanRepository(): SubscriptionPlanRepository
    fun subscriptionPackageRepository(): SubscriptionPackageRepository
    fun subscriptionIdFetcherUseCase(): SubscriptionIdFetcherUseCase
    fun formatUssdUseCase(): FormatUssdUseCase
}