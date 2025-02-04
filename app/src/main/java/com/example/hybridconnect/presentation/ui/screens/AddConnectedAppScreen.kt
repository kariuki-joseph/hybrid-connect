package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.utils.SnackbarManager
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.viewmodel.AddConnectedAppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddConnectedAppScreen(
    navController: NavHostController,
    viewModel: AddConnectedAppViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val connectId by viewModel.connectId.collectAsState()
    val appName by viewModel.appName.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val connectSuccess by viewModel.connectSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            SnackbarManager.showMessage(scope, it)
            viewModel.resetErrorMessage()
        }
    }

    LaunchedEffect(connectSuccess) {
        if (connectSuccess){
            SnackbarManager.showMessage(scope, "App connected successfully")
            delay(2000)
            viewModel.resetConnectSuccess()
            navController.popBackStack()

        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Connect New App") },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            withContext(Dispatchers.Main) {
                                navController?.popBackStack()
                            }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .imePadding()
        ) {

            AddConnectedAppScreenContent(
                connectId = connectId,
                appName = appName,
                isLoading = isLoading,
                connectSuccess = connectSuccess,
                onConnectIdChanged = { viewModel.onConnectIdChanged(it) },
                onAppNameChanged = { viewModel.onAppNameChanged(it) },
                onTryConnect = { viewModel.attemptConnect() }
            )
        }
    }
}

@Composable
fun AddConnectedAppScreenContent(
    connectId: String,
    appName: String,
    isLoading: Boolean = false,
    connectSuccess: Boolean = false,
    onConnectIdChanged: (String) -> Unit,
    onAppNameChanged: (String) -> Unit,
    onTryConnect: () -> Unit,
) {
    var hasFocus by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Column {
        OutlinedTextField(
            value = connectId,
            onValueChange = { newValue: String ->
                if (newValue.length <= 5 && newValue.matches(Regex("[A-Za-z0-9]*"))) {
                    onConnectIdChanged(newValue)
                }
            },
            label = { Text("Connect ID") },
            placeholder = { Text("5-Digit Connect ID") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Ascii,
                capitalization = KeyboardCapitalization.Characters
            ),
            modifier = Modifier
                .fillMaxWidth()
                .width(300.dp)
                .onFocusChanged { focusState ->
                    hasFocus = focusState.isFocused
                }
                .focusRequester(focusRequester),
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp
            ),
            leadingIcon = if (hasFocus || connectId.isNotEmpty()) {
                { Text("BHC-", modifier = Modifier.padding(start = 5.dp)) }
            } else null
        )

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = appName,
            onValueChange = { newValue: String ->
                if (newValue.length <= 30) {
                    onAppNameChanged(newValue)
                }
            },
            label = { Text("App Name") },
            placeholder = { Text("Desired App Name") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Ascii,
                capitalization = KeyboardCapitalization.Words
            ),
            modifier = Modifier
                .fillMaxWidth()
                .width(300.dp),
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp
            ),
        )


        Spacer(modifier = Modifier.height(16.dp))

        CustomButton(
            primary = true,
            onClick = onTryConnect,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                if (connectSuccess) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Connected",
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Text(
                        "Connect",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddConnectedAppScreenPreview() {
    AddConnectedAppScreenContent(
        connectId = "458834",
        appName = "Hybrid Main",
        isLoading = false,
        connectSuccess = false,
        onConnectIdChanged = {},
        onAppNameChanged = {},
        onTryConnect = {}
    )
}