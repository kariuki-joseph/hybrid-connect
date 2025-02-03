package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.repository.CommissionRateRepository
import javax.inject.Inject
import kotlin.math.roundToInt

class GetCommissionUseCase @Inject constructor(
    private val commissionRateRepository: CommissionRateRepository
) {
    suspend operator fun invoke(amount: Int): Int{
        return (commissionRateRepository.getCommissionRateForAmount(amount) * amount).roundToInt()
    }
}