package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SimCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hybridconnect.domain.enums.SimCard
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.viewmodel.SettingsViewModel

@Composable
fun SimSetup(onSimSetup: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val paymentSimCards by viewModel.paymentSimCards.collectAsState()
    val ussdSimCard by viewModel.ussdSimCard.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Sim Setup",
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SIM to receive payments",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 30.dp)
        )
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.SimCard, contentDescription = "SIM")
            Text(text = "SIM 1", modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = paymentSimCards.contains(SimCard.SIM_ONE),
                onCheckedChange = { viewModel.onPaymentSimChanged(SimCard.SIM_ONE) }
            )
        }

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.SimCard, contentDescription = "SIM")
            Text(text = "SIM 2", modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = paymentSimCards.contains(SimCard.SIM_TWO),
                onCheckedChange = { viewModel.onPaymentSimChanged(SimCard.SIM_TWO) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "HybridConnect SIM (To run USSDs)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 30.dp)
        )

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.SimCard, contentDescription = "SIM")
            Text(text = "SIM 1", modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = ussdSimCard == SimCard.SIM_ONE,
                onCheckedChange = {
                    if (it) viewModel.setUssdSimCard(SimCard.SIM_ONE)
                }
            )
        }

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.SimCard, contentDescription = "SIM")
            Text(text = "SIM 2", modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = ussdSimCard == SimCard.SIM_TWO,
                onCheckedChange = {
                    if (it) viewModel.setUssdSimCard(SimCard.SIM_TWO)
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        CustomButton(
            onClick = {
                viewModel.updateSimSettings()
                onSimSetup()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Finish")
        }
    }
}