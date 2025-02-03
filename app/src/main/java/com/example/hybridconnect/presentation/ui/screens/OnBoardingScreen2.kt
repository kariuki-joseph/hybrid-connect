package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hybridconnect.R
import com.example.hybridconnect.presentation.ui.components.CustomButton

@Composable
fun OnBoardingScreen2(
    modifier: Modifier = Modifier,
    onPrev: () -> Unit,
    onNext: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(26.dp))
                    Text(
                        text = "Setting up instructions",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colorResource(id = R.color.secondary),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ensure you have your HybridConnect SIM Card to process customer requests and the till SIM Card to receive payments",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CustomButton(
                            primary = false,
                            onClick = onPrev
                        ) {
                            Text(
                                text = "Prev",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                ),
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        CustomButton(
                            onClick = onNext
                        ) {
                            Text(
                                text = "Next",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                ),
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )
                        }
                    }
                }

            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewOnBoardingScreen2() {
    OnBoardingScreen2(
        onPrev = {},
        onNext = {},
    )
}