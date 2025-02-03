package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.repository.CommissionRateRepository
import javax.inject.Inject

class RefreshCommissionRatesUseCase @Inject constructor(
    private val commissionRateRepository: CommissionRateRepository
) {
    suspend operator fun invoke() {
        return commissionRateRepository.refreshCommissionRates()
    }
}