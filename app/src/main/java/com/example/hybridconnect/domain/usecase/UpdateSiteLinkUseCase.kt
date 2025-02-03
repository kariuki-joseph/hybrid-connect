package com.example.hybridconnect.domain.usecase

import android.util.Log
import com.example.hybridconnect.domain.model.SiteLink
import com.example.hybridconnect.domain.repository.SiteLinkRepository
import javax.inject.Inject

private const val TAG = "UpdateSiteLinkUseCase"

class UpdateSiteLinkUseCase @Inject constructor(
    private val siteLinkRepository: SiteLinkRepository,
) {
    suspend operator fun invoke(updatedSiteLink: SiteLink) {
        try {
            siteLinkRepository.updateSiteLinkRemote(updatedSiteLink)
            siteLinkRepository.updateSiteLinkLocal(updatedSiteLink)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            e.printStackTrace()
            throw e
        }
    }
}