package com.example.hybridconnect.presentation.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.enums.SubscriptionType
import com.example.hybridconnect.domain.model.SubscriptionPackage
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.CustomButton
import com.example.hybridconnect.presentation.ui.components.SubscriptionItemComponent
import com.example.hybridconnect.presentation.viewmodel.SubscriptionsViewModel
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySubscriptionScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    viewModel: SubscriptionsViewModel = hiltViewModel(),
) {
    val packages by viewModel.packages.collectAsState()
    val chosenPackage by viewModel.chosenPackage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoadingSubscriptionPlans by viewModel.isLoadingSubscriptionPlans.collectAsState()

    val unlimitedPlanLimit by viewModel.unlimitedPlanLimit.collectAsState()
    val limitedPlanLimit by viewModel.limitedPlanLimit.collectAsState()

    val unlimitedPlanFormatted = remember(unlimitedPlanLimit) {
        val totalMinutes = unlimitedPlanLimit / (1000 * 60)
        val hours = totalMinutes / 60
        val days = hours / 24
        val remainingHours = hours % 24
        val minutes = totalMinutes % 60
        String.format(Locale.getDefault(), "%dd %02dh %02dmin", days, remainingHours, minutes)
    }

    Log.d("Render", "Rendering Page")
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Subscription Plans"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (isLoadingSubscriptionPlans) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(top = 16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else if (errorMessage != null) {
                Text(
                    text = "Error: $errorMessage",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = { navController?.popBackStack() },
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            ),
                            text = "Go Back"
                        )
                    }
                }

            } else {
                MySubscriptionScreenContent(
                    unlimitedPlanBal = unlimitedPlanFormatted,
                    limitedPlanBal = limitedPlanLimit,
                    plans = packages,
                    selectedPlan = chosenPackage,
                    onSelectPlan = { subscription -> viewModel.setChosenPlan(subscription) },
                    onContinue = {
                        chosenPackage?.let {
                            navController?.navigate(Route.PayWithAirtime.name)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MySubscriptionScreenContent(
    unlimitedPlanBal: String,
    limitedPlanBal: Long,
    plans: List<SubscriptionPackage>,
    selectedPlan: SubscriptionPackage?,
    onSelectPlan: (SubscriptionPackage) -> Unit,
    onContinue: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Balance",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold)
                    ) {
                        append("Unlimited: ")
                    }
                    append(unlimitedPlanBal)
                },
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold)
                    ) {
                        append("Limited: ")
                    }
                    append("$limitedPlanBal Tokens")
                },
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "All Plans",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(plans) { subscription ->
                SubscriptionItemComponent(
                    subscriptionPackage = subscription,
                    onClick = { onSelectPlan(subscription) },
                    isActive = selectedPlan?.id == subscription.id
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CustomButton(
                onClick = onContinue,
                enabled = selectedPlan != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Subscribe",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MySubscriptionScreenPreview() {
    val unlimitedPlanBal = "2 days"
    val limitedPlanLimit = 2L

    val subscriptionPackages = listOf(
        SubscriptionPackage(
            id = UUID.randomUUID(),
            name = "Daily Plan",
            price = 20,
            description = "Daily plan",
            limit = 24.0,
            type = SubscriptionType.UNLIMITED,
        ),
        SubscriptionPackage(
            id = UUID.randomUUID(),
            name = "Weekly Plan",
            price = 100,
            description = "Weekly plan",
            limit = 726.0,
            type = SubscriptionType.UNLIMITED,
        ),
        SubscriptionPackage(
            id = UUID.randomUUID(),
            name = "100 Tokens",
            price = 50,
            description = "Save 10/-",
            limit = 100.0,
            type = SubscriptionType.UNLIMITED,
        ),
        SubscriptionPackage(
            id = UUID.randomUUID(),
            name = "Monthly Plan",
            price = 10000,
            description = "Daily plan",
            limit = 1020.0,
            type = SubscriptionType.UNLIMITED,
        )
    )

    MySubscriptionScreenContent(
        unlimitedPlanBal = unlimitedPlanBal,
        limitedPlanBal = limitedPlanLimit,
        plans = subscriptionPackages,
        selectedPlan = subscriptionPackages[1],
        onSelectPlan = {},
        onContinue = {}
    )
}
