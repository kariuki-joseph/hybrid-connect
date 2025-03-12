package com.example.hybridconnect.domain.repository

import com.example.hybridconnect.domain.enums.SimCard
import com.example.hybridconnect.domain.enums.AppSetting
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val isAppActive: StateFlow<Boolean>
    suspend fun saveSetting(setting: AppSetting, value: String)
    suspend fun getSetting(setting: AppSetting): String
    suspend fun deleteSetting(setting: AppSetting)
    suspend fun setOnBoardingCompleted()
    suspend fun isOnBoardingCompleted(): Boolean
    suspend fun setUssdSimCard(simCard: SimCard)
    suspend fun getUssdSimCard(): SimCard
    suspend fun setPaymentSimCards(simCards: List<SimCard>)
    suspend fun getPaymentsSimCards(): List<SimCard>
    suspend fun setAppActive(isActive: Boolean)
    fun isAppActive(): Boolean
    fun getAccessToken(): String
    suspend fun saveAccessToken(token: String)
    suspend fun getMaxUssdRetries(): Int
    suspend fun getMessageBufferSize(): Int
}