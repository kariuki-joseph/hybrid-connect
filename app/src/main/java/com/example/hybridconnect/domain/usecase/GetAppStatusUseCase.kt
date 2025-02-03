package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.repository.PrefsRepository
import javax.inject.Inject

class GetAppStatusUseCase @Inject constructor(
    private val prefsRepository: PrefsRepository,
) {
    operator fun invoke(): Boolean {
        return prefsRepository.isAppActive()
    }
}