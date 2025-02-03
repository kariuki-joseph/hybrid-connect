package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.hybridconnect.R
import com.example.hybridconnect.presentation.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayWithScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pay With") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            PayWithScreenContent(
                onMpesaSelected = {
                    navController?.navigate(Route.PayWithMpesa.name)
                },
                onAirtimeSelected = {
                    navController?.navigate(Route.PayWithAirtime.name)
                }
            )
        }
    }
}


@Composable
fun PayWithScreenContent(
    onMpesaSelected: () -> Unit = {},
    onAirtimeSelected: () -> Unit = {},
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAirtimeSelected() },
                    colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.secondary)
                    )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Airtime",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Go to Airtime",
                    tint = Color.White
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onMpesaSelected() },
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.secondary)
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "M-Pesa",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Go to M-Pesa",
                    tint = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PayWithScreenPreview() {
    PayWithScreenContent()
}