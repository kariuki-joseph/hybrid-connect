package com.example.hybridconnect.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hybridconnect.R
import com.example.hybridconnect.domain.model.Agent
import java.util.UUID

@Composable
fun ProfileScreenContent(
    agent: Agent,
    onEditProfile: () -> Unit,
) {
    Box {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
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
                Text(
                    text = agent.firstName + " " + agent.lastName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd),
            onClick = { onEditProfile() },
        ) {
            Icon(
                modifier = Modifier
                    .size(42.dp)
                    .padding(8.dp),
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenContentPreview() {
    ProfileScreenContent(
        agent = Agent(
            id = UUID.randomUUID(),
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "0758826552",
            email = "john.doe@example.com",
            pin = "0000",
        ),
        onEditProfile = {
            // Mock action for preview
        }
    )
}
