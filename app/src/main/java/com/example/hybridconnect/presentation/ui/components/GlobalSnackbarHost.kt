package com.example.hybridconnect.presentation.ui.components


import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.hybridconnect.domain.utils.SnackbarManager

@Composable
fun GlobalSnackbarHost() {
    val snackbarHostState = remember { SnackbarHostState() }
    SnackbarManager.init(snackbarHostState)
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .padding(WindowInsets.ime.asPaddingValues())
    )
}