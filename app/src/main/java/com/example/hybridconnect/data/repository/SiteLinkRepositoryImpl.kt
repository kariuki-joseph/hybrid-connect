package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.local.dao.SiteLinkDao
import com.example.hybridconnect.data.mappers.toApiError
import com.example.hybridconnect.data.mappers.toDomain
import com.example.hybridconnect.data.mappers.toEntity
import com.example.hybridconnect.data.remote.api.ApiService
import com.example.hybridconnect.data.remote.api.request.AddSiteLinkOfferRequest
import com.example.hybridconnect.data.remote.api.request.SiteLinkRequest
import com.example.hybridconnect.data.remote.api.request.UpdateSiteLinkRequest
import com.example.hybridconnect.domain.enums.SiteLinkAccountType
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.SiteLink
import com.example.hybridconnect.domain.repository.SiteLinkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "SiteLinkRepositoryImpl"

class SiteLinkRepositoryImpl(
    private val siteLinkDao: SiteLinkDao,
    private val apiService: ApiService,
) : SiteLinkRepository {
    private val _siteLink = MutableStateFlow<SiteLink?>(null)
    override val siteLink: StateFlow<SiteLink?> = _siteLink.asStateFlow()

    override suspend fun getSavedSiteLink(): SiteLink? {
        val siteLink = siteLinkDao.getSiteLink()
        siteLink?.let {
            _siteLink.value = it.toDomain()
        }
        return siteLink?.toDomain()
    }

    override suspend fun updateSiteLinkLocal(siteLink: SiteLink) {
        try {
            val existingSiteLink = siteLinkDao.getSiteLink()
            if (existingSiteLink == null) {
                siteLinkDao.insert(siteLink.toEntity())
            } else {
                siteLinkDao.update(siteLink.toEntity())
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }

    override suspend fun activateSiteLink() {
        try {
            val siteLink = siteLinkDao.getSiteLink() ?: throw Exception("No SiteLink found")
            val response = apiService.activateSiteLink(siteLink.id)
            if (!response.isSuccessful) {
                val errorCode = response.code()
                Log.e(TAG, "Error code: $errorCode, Error message: ${response.errorBody()}")
                throw Exception(
                    response.errorBody()?.toApiError()?.message ?: "Error activating SiteLink"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }

    override suspend fun deactivateSiteLink() {
        try {
            val siteLink = siteLinkDao.getSiteLink() ?: throw Exception("No SiteLink found")

            val response = apiService.deactivateSiteLink(siteLink.id)
            if (!response.isSuccessful) {
                val errorCode = response.code()
                Log.e(TAG, "Error code: $errorCode, Error message: ${response.errorBody()}")
                throw Exception(
                    response.errorBody()?.toApiError()?.message ?: "Error deactivating SiteLink"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }

    override suspend fun requestSiteLink(
        siteName: String,
        accountNumber: String,
        accountType: SiteLinkAccountType,
    ) {
        val siteLinkRequest = SiteLinkRequest(
            siteName = siteName,
            accountNumber = accountNumber,
            accountType = accountType
        )
        try {
            val response = apiService.requestSiteLink(siteLinkRequest)
            if (!response.isSuccessful) {
                val errorCode = response.code()
                Log.e(TAG, "Error code: $errorCode, Error message: ${response.errorBody()}")
                throw Exception(
                    response.errorBody()?.toApiError()?.message ?: "Error generating SiteLink"
                )
            }

            response.body()?.data?.let { site ->
                val siteLink = SiteLink(
                    id = site.siteLinkId,
                    siteName = site.siteName,
                    url = site.siteLinkUrl,
                    accountNumber = accountNumber,
                    accountType = accountType
                )
                siteLinkDao.insert(siteLink.toEntity())
                _siteLink.value = siteLink
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            throw e
        }
    }

    override suspend fun updateSiteLinkRemote(siteLink: SiteLink) {
        try {
            val updateRequest = UpdateSiteLinkRequest(
                siteLinkId = siteLink.id,
                siteName = siteLink.siteName,
                accountType = siteLink.accountType.name,
                accountNumber = siteLink.accountNumber
            )

            val response = apiService.updateSiteLink(siteLink.id, updateRequest)

            if (!response.isSuccessful) {
                val errorCode = response.code()
                Log.e(TAG, "Error code: $errorCode, Error message: ${response.errorBody()}")
                throw Exception(
                    response.errorBody()?.toApiError()?.message ?: "Error updating SiteLink"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            throw e
        }
    }


    override suspend fun deleteSiteLinkRemote(siteLink: SiteLink) {
        try {
            val response = apiService.deleteSiteLink(siteLink.id)
            if (!response.isSuccessful) {
                val errorCode = response.code()
                Log.e(TAG, "Error code: $errorCode, Error message: ${response.errorBody()}")
                throw Exception(
                    response.errorBody()?.toApiError()?.message
                        ?: "Error deleting SiteLink from API"
                )
            }
            _siteLink.value = null
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Unknown error deleting SiteLink")
            throw e
        }
    }

    override suspend fun deleteSiteLinkLocal(siteLink: SiteLink) {
        try {
            siteLinkDao.deleteSiteLink(siteLink.toEntity())
            _siteLink.value = null
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Unknown error deleting SiteLink from device")
            throw e
        }
    }


    override suspend fun addSiteLinkOffer(offer: Offer) {
        try {
            val request = AddSiteLinkOfferRequest(
                offerId = offer.id.toString(),
                name = offer.name,
                ussdCode = offer.ussdCode,
                price = offer.price,
                tag = offer.tag,
                type = offer.type
            )
            val response = apiService.addSiteLinkOffer(request)
            if (!response.isSuccessful) {
                throw Exception(
                    response.errorBody().toApiError()?.message
                        ?: "Error Syncing SiteLink offer. Please try again"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            throw e
        }
    }

    override suspend fun deleteSiteLinkOffer(offer: Offer) {
        try {
            val response = apiService.deleteSiteLinkOffer(offer.id.toString())
            if (!response.isSuccessful) {
                val errorMessage = response.errorBody().toApiError()?.message
                throw Exception(errorMessage ?: "Error Syncing SiteLink offer. Please try again")
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            throw e
        }
    }

    override suspend fun getSiteLinkOffers(siteLink: SiteLink): List<Offer> {
        try {
            val response = apiService.getSiteLinkDetails(siteLink.id)
            if (!response.isSuccessful) {
                val errorMessage = response.errorBody().toApiError()?.message
                throw Exception(errorMessage ?: "Error getting SiteLink Offers. Please try again")
            }
            val offers = response.body()?.data?.offers

            return offers?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "getSiteLinkOffers", e)
            throw e
        }
    }
}