package com.example.hybridconnect.data.repository

import com.example.hybridconnect.data.enums.PrefKey
import com.example.hybridconnect.domain.enums.SimCard
import com.example.hybridconnect.data.local.dao.PrefsDao
import com.example.hybridconnect.data.local.entity.PrefEntity
import com.example.hybridconnect.data.local.preferences.SharedPrefsManager
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.repository.PrefsRepository
import com.example.hybridconnect.domain.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefsRepositoryImpl @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager,
    private val prefsDao: PrefsDao,
) : PrefsRepository {
    private val _isAppActive = MutableStateFlow(false)
    override val isAppActive: StateFlow<Boolean> = _isAppActive.asStateFlow()

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

    init {
        getAppActiveStatus()
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

    override suspend fun setAppActive(isActive: Boolean) {
        val activeStatus = PrefEntity(Constants.IS_APP_ACTIVE, isActive.toString())
        prefsDao.set(activeStatus)
        _isAppActive.value = isActive
    }

    override fun isAppActive(): Boolean {
        return isAppActive.value
    }

    override fun getAccessToken(): String {
        return sharedPrefsManager.get(Constants.KEY_ACCESS_TOKEN, "")
    }

    override suspend fun saveAccessToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            sharedPrefsManager.set(Constants.KEY_ACCESS_TOKEN, token)
        }
    }

    override suspend fun getMaxUssdRetries(): Int {
        val pref = prefsDao.getSetting(Constants.KEY_MAX_USSD_RETRIES)
        return pref?.value?.toInt() ?: 3
    }

    private fun getAppActiveStatus() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = prefsDao.getSetting(Constants.IS_APP_ACTIVE)
            _isAppActive.value = result?.value?.toBoolean() ?: false
        }
    }
}