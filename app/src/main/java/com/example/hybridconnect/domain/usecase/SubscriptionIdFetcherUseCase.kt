package com.example.hybridconnect.domain.usecase

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SubscriptionManager
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.repository.SettingsRepository
import javax.inject.Inject

class SubscriptionIdFetcherUseCase @Inject constructor(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {

    @SuppressLint("MissingPermission")
    suspend fun getUssdSubscriptionId(): Int {
        val useSim2 = settingsRepository.getSetting(AppSetting.DIAL_USSD_VIA_SIM_2).toBoolean()
        val simSlot = if(useSim2) 2 else 1
        
        return getSubscriptionIdForSimSlot(simSlot) ?: throw Exception("Invalid SIM Card for Dialing USSD")
    }


    @SuppressLint("MissingPermission")
    private fun getSubscriptionIdForSimSlot(simSlot: Int): Int? {
        val simSlotIndex = simSlot -1
        val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val activeSubscriptionInfoList = subscriptionManager.activeSubscriptionInfoList ?: return null
        return activeSubscriptionInfoList.getOrNull(simSlotIndex)?.subscriptionId
    }
}