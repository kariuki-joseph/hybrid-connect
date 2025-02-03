package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hybridconnect.domain.enums.SubscriptionType
import com.example.hybridconnect.domain.model.SubscriptionPackage
import java.util.UUID

@Composable
fun SubscriptionItemComponent(
    subscriptionPackage: SubscriptionPackage,
    onClick: (SubscriptionPackage) -> Unit,
    isActive: Boolean = false,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val lighterPrimaryColor = primaryColor.copy(alpha = 0.5f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(subscriptionPackage) }
            .border(
                width = 2.dp,
                color = if (isActive) lighterPrimaryColor else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = subscriptionPackage.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.W500
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "KES ${subscriptionPackage.price} /-",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.W600
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                subscriptionPackage.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.W400
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SubscriptionItemComponentPreview() {
    SubscriptionItemComponent(
        subscriptionPackage = SubscriptionPackage(
            id = UUID.randomUUID(),
            name = "Weekly Plan",
            price = 1000,
            limit = (10 * 60 * 10).toDouble(),
            description = "Save KES 10/-",
            type = SubscriptionType.UNLIMITED
        ),
        onClick = {},
    )

}