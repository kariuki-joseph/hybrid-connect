package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.utils.formatCurrency
import com.example.hybridconnect.domain.utils.formatTimeAgo
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.viewmodel.CustomersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCustomersScreen(
    navController: NavHostController? = null,
    modifier: Modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 8.dp),
    viewModel: CustomersViewModel = hiltViewModel(),
) {
    val customers by viewModel.customers.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Balances") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        CustomersScreenContent(
            modifier = modifier.padding(paddingValues),
            customers = customers,
            onCustomerClick = { customer -> navController?.navigate(Route.EditCustomer.name.replace("{customerPhone}", customer.phone))}
        )
    }
}

@Composable
fun CustomersScreenContent(
    modifier: Modifier = Modifier,
    customers: List<Customer>,
    onCustomerClick: (Customer) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .background(MaterialTheme.colorScheme.surface),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Name",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Phone",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Bal (Ksh)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Last Purchase",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(customers) { customer ->
                CustomerItem(customer = customer, onCustomerClick = onCustomerClick)
            }
        }
    }
}

@Composable
fun CustomerItem(
    customer: Customer,
    onCustomerClick: (Customer) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCustomerClick(customer) }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = customer.name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )
        Text(
            text = customer.phone,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
        Text(
            text = formatCurrency(customer.accountBalance),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = formatTimeAgo(customer.lastPurchaseTime),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun CustomerScreenPreview() {
    val customers = listOf(
        Customer(
            id = 1,
            name = "John Doe",
            phone = "0758826552",
            accountBalance = 2500,
            lastPurchaseTime = System.currentTimeMillis() - 86400000
        ),
        Customer(
            id = 2,
            name = "Kim Jong",
            phone = "0758826553",
            accountBalance = 20,
            lastPurchaseTime = System.currentTimeMillis() - 86400000
        )
    )
    CustomersScreenContent(
        customers = customers,
        onCustomerClick = {}
    )
}