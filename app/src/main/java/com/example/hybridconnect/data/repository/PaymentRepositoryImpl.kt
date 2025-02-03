package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.mappers.toApiError
import com.example.hybridconnect.data.remote.api.ApiService
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.model.SubscriptionPackage
import com.example.hybridconnect.domain.repository.PaymentRepository
import com.example.hybridconnect.domain.repository.PrefsRepository
import com.example.hybridconnect.domain.utils.formatPhoneToTenDigits
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PaymentRepositoryImpl"

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val prefsRepository: PrefsRepository,
) : PaymentRepository {

    private var _subscriptionPackage: SubscriptionPackage? = null

    override fun setSubscription(subscriptionPackage: SubscriptionPackage) {
        _subscriptionPackage = subscriptionPackage
    }

    override fun getSubscription(): SubscriptionPackage? {
        return _subscriptionPackage
    }

    override suspend fun getAdminSubscriptionNumber(): String {
        try {
            val response = apiService.getSubscriptionNumbers()
            if (!response.isSuccessful) {
                val errorMessage = response.errorBody()?.toApiError()?.message
                throw Exception(errorMessage ?: "Error fetching subscription numbers")
            }

            if (response.body() == null || response.body()?.data.isNullOrEmpty()) {
                throw Exception("Empty or invalid response for subscription numbers received. Please try again.")
            }

            val activeNumber = response.body()?.data?.firstOrNull { it.isActive }
            if (activeNumber == null) {
                // fallback to local
                val cachedNumber = prefsRepository.getSetting(AppSetting.ADMIN_PAYMENT_NUMBER)
                if (cachedNumber.isNotBlank()) {
                    Log.w(TAG, "Returning cached subscription number: $cachedNumber")
                    return cachedNumber
                } else {
                    throw Exception("Seems the admin has not set the subscription number. Please contact support for further assistance")
                }
            }

            prefsRepository.saveSetting(
                AppSetting.ADMIN_PAYMENT_NUMBER,
                formatPhoneToTenDigits(activeNumber.phone)
            )
            return activeNumber.phone
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching subscription number from API: ${e.message}")
            throw e
        }
    }

    override suspend fun getAdminSiteLinkNumber(): String {
        try {
            val response = apiService.getSiteLinkNumbers()

            if (!response.isSuccessful) {
                val errorMessage = response.errorBody()?.toApiError()?.message
                throw Exception(errorMessage ?: "Error fetching site link numbers")
            }

            if (response.body() == null || response.body()?.data.isNullOrEmpty()) {
                throw Exception("No SiteLink phone number has been set by admin.")
            }

            val activeNumber = response.body()?.data?.firstOrNull { it.isActive }
            if (activeNumber == null) {
                // Fallback: Try fetching from local storage
                val cachedNumber = prefsRepository.getSetting(AppSetting.ADMIN_SITE_LINK_NUMBER)
                if (cachedNumber.isNotBlank()) {
                    Log.w(TAG, "Returning cached site link number: $cachedNumber")
                    return cachedNumber
                } else {
                    throw Exception("No active SiteLink phone number found. Please contact admin for support")
                }
            }

            prefsRepository.saveSetting(
                AppSetting.ADMIN_SITE_LINK_NUMBER,
                formatPhoneToTenDigits(activeNumber.phone)
            )

            return activeNumber.phone
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching site link number from API: ${e.message}")
            throw e
        }
    }

}
