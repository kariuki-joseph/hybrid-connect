package com.example.hybridconnect.domain.usecase

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "LoginCoordinator"

@Singleton
class LoginCoordinator @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
) {
    suspend operator fun invoke(email: String, pin: String) {
        try {
            loginUserUseCase(email, pin)
        } catch (e: Exception) {
            Log.e(TAG, "LoginUser", e)
            throw e
        }
    }
}
