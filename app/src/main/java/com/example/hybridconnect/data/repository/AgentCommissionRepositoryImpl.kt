package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.local.dao.AgentCommissionDao
import com.example.hybridconnect.data.mappers.toDomain
import com.example.hybridconnect.data.mappers.toEntity
import com.example.hybridconnect.domain.enums.OfferTag
import com.example.hybridconnect.domain.model.AgentCommission
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.repository.AgentCommissionRepository
import com.example.hybridconnect.domain.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "AgentCommissionRepositoryImpl"

class AgentCommissionRepositoryImpl(
    private val agentCommissionDao: AgentCommissionDao,
) : AgentCommissionRepository {
    private val _agentCommissions = MutableStateFlow<List<AgentCommission>>(emptyList())
    override val agentCommissions: StateFlow<List<AgentCommission>> =
        _agentCommissions.asStateFlow()

    override suspend fun addCommission(commission: AgentCommission) {
        val commissionExists = agentCommissionDao.getCommissionForDate(commission.date) != null
        if (commissionExists) {
            agentCommissionDao.incrementCommissionForDate(commission.date, commission.amount)
            _agentCommissions.value = _agentCommissions.value.map {
                if (it.date == commission.date) it.copy(amount = it.amount + commission.amount) else it
            }
            return
        }
        agentCommissionDao.insertCommission(commission.toEntity())
        _agentCommissions.value += commission
    }

    override suspend fun getCommissions(): List<AgentCommission> {
        return agentCommissionDao.getAllCommissions().map { it.toDomain() }
    }

    override fun getCommissionForOffer(offer: Offer): Double {
        try {
            val eligibleCommissionsMap = mapOf(
                OfferTag.OFFER_1 to 5.0,
                OfferTag.OFFER_2 to 5.0,
                OfferTag.OFFER_3 to 30.0,
                OfferTag.OFFER_4 to 70.0,
                OfferTag.OFFER_5 to 2.0,
                OfferTag.OFFER_6 to 2.0,
                OfferTag.OFFER_7 to 9.9,
                OfferTag.OFFER_8 to 5.5,
            )
            return eligibleCommissionsMap[offer.tag] ?: 0.0
        } catch (e: Exception) {
            Log.e(TAG, "getCommissionForOfferId", e)
            throw e
        }
    }

    override suspend fun fetchCommissionForDates(dates: List<String>): List<AgentCommission> {
        val agentCommissions = dates.map { date ->
            agentCommissionDao.getCommissionForDate(date)?.toDomain() ?: AgentCommission(date, 0.0)
        }
        _agentCommissions.value = agentCommissions
        return agentCommissions
    }
}