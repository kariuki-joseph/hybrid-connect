package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hybridconnect.domain.enums.OfferType
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.utils.getIconForOfferType
import java.util.UUID

@Composable
fun SiteLinkOfferItem(
    offer: Offer,
    onToggleSync: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = 8.dp
            )
        ) {
            Icon(
                imageVector = getIconForOfferType(offer.type),
                contentDescription = "Offer Type",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = offer.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Text(
                        text = "KES ${offer.price}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = offer.ussdCode,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alignByBaseline()
                    )
                    Switch(
                        checked = offer.isSiteLinked,
                        onCheckedChange = { onToggleSync(offer.isSiteLinked) },
                        modifier = Modifier.scale(.8f)
                    )

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SiteLinkOfferItemPreview() {
    SiteLinkOfferItem(
        offer = Offer(
            id = UUID.randomUUID(),
            name = "1.5GB for 3hrs",
            price = 100,
            ussdCode = "*123*1#",
            type = OfferType.VOICE
        ),
        onToggleSync = { }
    )
}