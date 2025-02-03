package com.example.hybridconnect.domain.utils

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object SnackbarManager {
    private var snackbarHostState: SnackbarHostState? = null

    fun init(snackbarHostState: SnackbarHostState) {
        SnackbarManager.snackbarHostState = snackbarHostState
    }

    fun showMessage(scope: CoroutineScope, message: String) {
        scope.launch {
            snackbarHostState?.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
}