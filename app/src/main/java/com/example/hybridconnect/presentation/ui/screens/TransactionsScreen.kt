package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.presentation.dto.FilterChipState
import com.example.hybridconnect.presentation.ui.components.DurationFilterChips
import com.example.hybridconnect.presentation.ui.components.FilterChips
import com.example.hybridconnect.presentation.ui.components.TransactionList
import com.example.hybridconnect.presentation.ui.components.TransactionsFilterChips
import com.example.hybridconnect.presentation.viewmodel.TransactionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: TransactionsViewModel = hiltViewModel(),
) {
    val selectedChip by viewModel.selectedChip.collectAsState()
    val selectedDurationFilterChip by viewModel.selectedDurationFilterChip.collectAsState()
    val transactions by viewModel.filteredTransactions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .padding(paddingValues)
        ) {
            TransactionsFilterChips(
                onChipSelected = { chip -> viewModel.setSelectedChip(chip) },
                selectedChip = selectedChip,
            )
            HorizontalDivider()
            DurationFilterChips(
                selectedChip = selectedDurationFilterChip,
                onChipSelected = {
                    viewModel.setSelectedDurationFilterChip(it)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TransactionList(
                autoScroll = false,
                transactions = transactions,
                onTransactionClick = {
                    navController.navigate("transactions/${it.id}")
                },
                onRetry = { transaction -> viewModel.retryTransaction(transaction) }
            )
        }
    }
}