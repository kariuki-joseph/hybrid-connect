package com.example.hybridconnect.domain.usecase

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import javax.inject.Inject

class PermissionHandlerUseCase @Inject constructor(
    private val context: Context,
) {
    fun hasNecessaryPermissions(): Boolean {
        return hasCallPermissions() && hasReadPhoneStatePermissions()
    }

    fun requestNecessaryPermissions(activity: Activity) {
        if (!hasCallPermissions()) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.CALL_PHONE),
                CALL_PERMISSION_REQUEST_CODE
            )
        }

        if (!hasReadPhoneStatePermissions()) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.READ_PHONE_STATE),
                READ_PHONE_STATE_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun hasCallPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasReadPhoneStatePermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val CALL_PERMISSION_REQUEST_CODE = 1
        private const val READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 2
    }
}