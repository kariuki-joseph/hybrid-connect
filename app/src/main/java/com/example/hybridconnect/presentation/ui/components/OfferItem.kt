package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hybridconnect.R
import com.example.hybridconnect.domain.enums.OfferType
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.utils.getIconForOfferType
import java.util.UUID

@Composable
fun OfferItem(
    offer: Offer,
    onEditClick: (Offer) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.secondary),
                shape = MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .height(104.dp)
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
                            ),
                        )

                        Text(
                            text = "KES ${offer.price}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "KES ${offer.ussdCode}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.alignByBaseline()
                        )
                        IconButton(
                            onClick = { onEditClick(offer) },
                            modifier = Modifier.size(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier
                                    .size(42.dp)
                                    .padding(8.dp)
                                    .alignByBaseline(),
                            )
                        }

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OfferItemPreview() {
    OfferItem(
        offer = Offer(
            id = UUID.randomUUID(),
            name = "1.5GB for 3hrs",
            price = 100,
            ussdCode = "*123*1#",
            type = OfferType.VOICE
        ),
        onEditClick = { /* Preview action */ }
    )
}