package com.example.hybridconnect.data.repository

import com.example.hybridconnect.data.enums.PrefKey
import com.example.hybridconnect.data.local.dao.PrefsDao
import com.example.hybridconnect.data.local.entity.PrefEntity
import com.example.hybridconnect.data.local.preferences.SharedPrefsManager
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.enums.AppState
import com.example.hybridconnect.domain.enums.SimCard
import com.example.hybridconnect.domain.repository.SettingsRepository
import com.example.hybridconnect.domain.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager,
    private val prefsDao: PrefsDao,
) : SettingsRepository {
    override suspend fun saveSetting(setting: AppSetting, value: String) {
        val pref = PrefEntity(setting.name, value)
        prefsDao.set(pref)
    }

    override suspend fun getSetting(setting: AppSetting): String {
        val pref = prefsDao.getSetting(setting.name)
        return pref?.value ?: ""
    }

    override suspend fun deleteSetting(setting: AppSetting) {
        val pref = prefsDao.getSetting(setting.name)
        pref?.let { prefsDao.deleteSetting(it) }
    }

    override suspend fun setOnBoardingCompleted() {
        sharedPrefsManager.set<Boolean>(PrefKey.ONBOARDING_COMPLETE.name, true)
    }

    override suspend fun isOnBoardingCompleted(): Boolean {
        return sharedPrefsManager.get<Boolean>(PrefKey.ONBOARDING_COMPLETE.name, false)
    }

    override suspend fun setUssdSimCard(simCard: SimCard) {
        sharedPrefsManager.set<String>(PrefKey.USSD_SIM_SLOT.name, simCard.name)
    }

    override suspend fun getUssdSimCard(): SimCard {
        val simCard =
            sharedPrefsManager.get<String>(PrefKey.USSD_SIM_SLOT.name, SimCard.SIM_ONE.name)
        return SimCard.valueOf(simCard)
    }

    override suspend fun setPaymentSimCards(simCards: List<SimCard>) {
        sharedPrefsManager.set<String>(PrefKey.PAYMENTS_SIM_CARDS.name, simCards.joinToString(","))
    }


    override suspend fun getPaymentsSimCards(): List<SimCard> {
        val simCards = sharedPrefsManager.get<String>(PrefKey.PAYMENTS_SIM_CARDS.name, "")
        return if (simCards.isEmpty()) {
            emptyList()
        } else {
            simCards.split(",").map { SimCard.valueOf(it) }
        }
    }

    override fun getAccessToken(): String {
        return sharedPrefsManager.get(Constants.KEY_ACCESS_TOKEN, "")
    }

    override suspend fun saveAccessToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            sharedPrefsManager.set(Constants.KEY_ACCESS_TOKEN, token)
        }
    }
}