package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.repository.PrefsRepository
import javax.inject.Inject

class DeactivateAppUseCase @Inject constructor(
    private val prefsRepository: PrefsRepository,
) {
    suspend operator fun invoke() {
        prefsRepository.setAppActive(false)
    }
}