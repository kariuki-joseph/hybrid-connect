package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.local.dao.AgentDao
import com.example.hybridconnect.data.mappers.toApiError
import com.example.hybridconnect.data.mappers.toAuthDetails
import com.example.hybridconnect.data.mappers.toDomain
import com.example.hybridconnect.data.mappers.toEntity
import com.example.hybridconnect.data.remote.api.ApiService
import com.example.hybridconnect.data.remote.api.request.ResendOtpRequest
import com.example.hybridconnect.data.remote.api.request.SignInRequest
import com.example.hybridconnect.data.remote.api.request.SignUpRequest
import com.example.hybridconnect.data.remote.api.request.UpdateProfileRequest
import com.example.hybridconnect.data.remote.api.request.VerifyOtpRequest
import com.example.hybridconnect.data.remote.api.response.UpdatePinRequest
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.model.Agent
import com.example.hybridconnect.domain.model.AuthDetails
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.repository.PrefsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

private const val TAG = "AuthRepositoryImpl"

class AuthRepositoryImpl(
    private val agentDao: AgentDao,
    private val prefsRepository: PrefsRepository,
    private val apiService: ApiService,
) : AuthRepository {

    private val _agent = MutableStateFlow<Agent?>(null)
    override val agent: StateFlow<Agent?> = _agent

    override suspend fun registerAgent(agent: Agent) {
        val signUpRequest = SignUpRequest(
            name = agent.firstName + " " + agent.lastName,
            email = agent.email,
            phone = agent.phoneNumber,
            pin = agent.pin
        )
        try {
            val response = apiService.signUp(signUpRequest)
            if (!response.isSuccessful) {
                throw Exception(
                    response.errorBody().toApiError()?.message
                        ?: "Error creating your account. Please try again"
                )
            }
            val token = response.body()?.data?.token
            if (token.isNullOrEmpty()) {
                throw Exception("Couldn't get registration token. Please try again")
            }
            prefsRepository.saveAccessToken(token)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveAgent(agent: Agent) {
        agentDao.deleteAgent(agent.toEntity()) // there can only be one logged in agent at a time
        agentDao.createAgent(agent.toEntity())
        _agent.value = agentDao.getAgent(agent.id)?.toDomain()
    }

    override suspend fun loginUser(email: String, pin: String): AuthDetails {
        val request = SignInRequest(email, pin)
        try {
            val response = apiService.signIn(request)
            if (!response.isSuccessful) {
                throw Exception(
                    response.errorBody()?.toApiError()?.message ?: "Invalid email or PIN"
                )
            }

            if (response.body() == null) {
                throw Exception("Null body received")
            }
            val signInResponse = response.body()?.data
            signInResponse?.agent?.toDomain()
                ?: throw Exception("Couldn't get complete agent info. Please log in again")


            return signInResponse.toAuthDetails()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }

    override suspend fun fetchAgent(): Agent {
        try {
            val agentId = prefsRepository.getSetting(AppSetting.AGENT_ID)
            if (agentId.isEmpty()) throw Exception("Seems you have been logged out")
            val agent = agentDao.getAgent(UUID.fromString(agentId))?.toDomain()
                ?: throw Exception("Agent not found")
            _agent.value = agent
            return agent
        } catch (e: Exception) {
            Log.e(TAG, "fetchAgent", e)
            throw e
        }
    }

    override suspend fun logoutUser(agent: Agent) {
        try {
            agentDao.deleteAgent(agent.toEntity())
            _agent.value = null
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw e
        }
    }

    override suspend fun updateAgentRemote(agent: Agent) {
        try {
            val updateProfileRequest = UpdateProfileRequest(
                name = agent.firstName + " " + agent.lastName,
                email = agent.email,
                phone = agent.phoneNumber,
                pin = agent.pin,
            )

            val response =
                apiService.updateProfile(agent.id.toString(), request = updateProfileRequest)

            if (!response.isSuccessful) {
                val errorMessage = response.errorBody().toApiError()?.message
                    ?: "Unable to update your profile at the moment. Please try again later"
                throw Exception(errorMessage)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            throw e
        }
    }

    override suspend fun sendOtp(email: String) {
        val resendOtpRequest = ResendOtpRequest(email = email)
        try {
            val response = apiService.resendOtp(resendOtpRequest)
            if (!response.isSuccessful) {
                throw Exception(
                    response.errorBody()?.toApiError()?.message
                        ?: "Unable to resend OTP at this time. Please try again later"
                )
            }
            val token = response.body()?.data?.token
                ?: throw Exception("Invalid OTP response, please try again later")
            prefsRepository.saveAccessToken(token)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to verify OTP ${e.message}")
            throw e
        }
    }

    override suspend fun verifyOtp(otp: String): Agent {
        val otpRequest = VerifyOtpRequest(otp = otp)
        try {
            val response = apiService.verifyOtp(otpRequest)
            if (!response.isSuccessful) {
                throw Exception(
                    response.errorBody()?.toApiError()?.message
                        ?: "Unable to verify OTP this time. Please try again later"
                )
            }

            val apiAgent = response.body()?.data
                ?: throw Exception("Could not get your account. You may need to register again")
            return apiAgent.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to verify OTP ${e.message}")
            throw e
        }
    }

    override suspend fun updateAgentPin(pin: String) {
        val request = UpdatePinRequest(pin)
        try {
            val response = apiService.updatePin(request)
            if (!response.isSuccessful) {
                throw Exception(
                    response.errorBody().toApiError()?.message
                        ?: "Error resetting PIN. Please try again"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "updateAgentPin", e)
            throw e
        }
    }
}