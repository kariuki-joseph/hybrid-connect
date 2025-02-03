package com.example.hybridconnect.domain.repository

import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.SiteLink
import com.example.hybridconnect.domain.enums.SiteLinkAccountType
import kotlinx.coroutines.flow.StateFlow

interface SiteLinkRepository {
    val siteLink: StateFlow<SiteLink?>
    suspend fun getSavedSiteLink(): SiteLink?
    suspend fun updateSiteLinkLocal(siteLink: SiteLink)
    suspend fun activateSiteLink()
    suspend fun deactivateSiteLink()
    suspend fun requestSiteLink(siteName: String, accountNumber: String, accountType: SiteLinkAccountType)
    suspend fun updateSiteLinkRemote(siteLink: SiteLink)
    suspend fun deleteSiteLinkRemote(siteLink: SiteLink)
    suspend fun deleteSiteLinkLocal(siteLink: SiteLink)
    suspend fun getSiteLinkOffers(siteLink: SiteLink): List<Offer>
    suspend fun addSiteLinkOffer(offer: Offer)
    suspend fun deleteSiteLinkOffer(offer: Offer)
}