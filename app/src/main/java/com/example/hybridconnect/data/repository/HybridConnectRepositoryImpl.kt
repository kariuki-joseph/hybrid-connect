package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.mappers.toApiError
import com.example.hybridconnect.data.remote.api.ApiService
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.repository.HybridConnectRepository
import com.example.hybridconnect.domain.repository.PrefsRepository
import javax.inject.Inject

private const val TAG = "HybridConnectRepositoryImpl"

class HybridConnectRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val prefsRepository: PrefsRepository,
) : HybridConnectRepository {
    override suspend fun generateConnectId(): String {
        try {
            val response = apiService.generateHybridConnectId()
            if (!response.isSuccessful) {
                val errorCode = response.code()
                Log.e(TAG, "Error code: $errorCode, Error message: ${response.errorBody()}")
                throw Exception(
                    response.errorBody()?.toApiError()?.message ?: "Error generating connectId"
                )
            }
            val connectId = response.body()?.data?.connectId ?: throw Exception("Generated connect ID is too short or empty. Please try again")
            prefsRepository.saveSetting(AppSetting.APP_CONNECT_ID, connectId)
            return connectId
        } catch (e: Exception) {
            Log.e(TAG, "generateConnectId", e)
            throw e
        }
    }
}