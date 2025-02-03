package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.R
import com.example.hybridconnect.domain.model.Agent
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.Keypad
import com.example.hybridconnect.presentation.ui.components.PinRow
import com.example.hybridconnect.presentation.viewmodel.PinLoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

@Composable
fun PinLoginScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    viewModel: PinLoginViewModel = hiltViewModel(),
    email: String = "",
) {
    val agent by viewModel.agent.collectAsState()
    val pin by viewModel.pin.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            withContext(Dispatchers.Main) {
                navController?.navigate(Route.Home.name){
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }
    LaunchedEffect(email) {
        viewModel.onEmailChanged(email)
    }

    PinLoginScreenContent(
        modifier = modifier,
        agent = agent,
        isLoading = isLoading,
        errorMessage = errorMessage,
        pin = pin,
        onNumberClick = { number -> viewModel.onNumberClick(number) },
        onDeletePin = { viewModel.onDeletePin() },
    )

}

@Composable
fun PinLoginScreenContent(
    modifier: Modifier = Modifier,
    agent: Agent?,
    pin: String,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onNumberClick: (number: String) -> Unit,
    onDeletePin: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)

                    )
                    agent?.let {
                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "${agent.firstName} ${agent.lastName}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight(500),
                            )
                        )

                        Text(
                            text = agent.email,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight(400),
                            )
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = errorMessage ?: "Enter your PIN",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Center,
                            color = if (errorMessage != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    PinRow(
                        pin = pin,
                        isLoading = isLoading,
                        hasError = errorMessage != null,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Keypad(
                        pin = pin,
                        onNumberClick = { number -> onNumberClick(number) },
                        onDeleteClick = { onDeletePin() }
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun PinLoginPreview() {
    val agent = Agent(
        id = UUID.randomUUID(),
        firstName = "John",
        lastName = "Doe",
        phoneNumber = "07588256553",
        email = "john.doe@gmail.com",
        pin = ""
    )

    PinLoginScreenContent(
        agent = agent,
        isLoading = false,
        pin = "123",
        onNumberClick = {},
        onDeletePin = {},
    )
}