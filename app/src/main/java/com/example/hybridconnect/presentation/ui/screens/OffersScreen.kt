package com.example.hybridconnect.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.hybridconnect.domain.enums.OfferType
import com.example.hybridconnect.presentation.navigation.Route
import com.example.hybridconnect.presentation.ui.components.OfferItem
import com.example.hybridconnect.presentation.ui.components.OfferScreenFilterChips
import com.example.hybridconnect.presentation.viewmodel.OffersViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OffersScreen(
    navController: NavHostController,
    viewModel: OffersViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 8.dp),
) {
    val offers by viewModel.filteredOffers.collectAsState()
    val selectedOfferType by viewModel.selectedOfferType.collectAsState()

    var isFabExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(if (isFabExpanded) 45f else 0f, label = "")

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column {
            TopAppBar(title = {
                Text(
                    "My Offers"
                )
            },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                })
            OfferScreenFilterChips(
                onOfferTypeSelected = { offerType -> viewModel.onOfferTypeSelected(offerType) },
                selectedOfferType = selectedOfferType
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(offers) { offer ->
                    OfferItem(
                        offer = offer,
                        onEditClick = {
                            navController.navigate("offers/edit/${offer.id}")
                        },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(84.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isFabExpanded) Color.Black.copy(alpha = 0.5f) else Color.Transparent)
                .then(
                    if (isFabExpanded) Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { isFabExpanded = false }
                    else Modifier
                )
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .padding(bottom = 32.dp, end = 32.dp)
                ) {
                    if (isFabExpanded) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .align(Alignment.End)
                        ) {
                            // AnimatedVisibility for Bundles
                            AnimatedVisibility(visible = isFabExpanded) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        viewModel.setOfferTypeToAdd(OfferType.DATA)
                                        navController.navigate(Route.AddOffer.name)
                                    }
                                ) {
                                    Text(
                                        text = "Data",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.animateEnterExit(
                                            enter = slideInHorizontally() + fadeIn(),
                                            exit = slideOutHorizontally() + fadeOut()
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    FloatingActionButton(
                                        onClick = {
                                            viewModel.setOfferTypeToAdd(OfferType.DATA)
                                            navController.navigate(Route.AddOffer.name)
                                        },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .animateEnterExit(
                                                enter = scaleIn() + fadeIn(),
                                                exit = scaleOut() + fadeOut()
                                            )
                                    ) {
                                        Icon(Icons.Default.Language, contentDescription = "Data")
                                    }
                                }
                            }

                            // AnimatedVisibility for Airtime
                            AnimatedVisibility(visible = isFabExpanded) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        viewModel.setOfferTypeToAdd(OfferType.VOICE)
                                        navController.navigate(Route.AddOffer.name)
                                    }
                                ) {
                                    Text(
                                        text = "Airtime",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.animateEnterExit(
                                            enter = slideInHorizontally() + fadeIn(),
                                            exit = slideOutHorizontally() + fadeOut()
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    FloatingActionButton(
                                        onClick = {
                                            viewModel.setOfferTypeToAdd(OfferType.VOICE)
                                            navController.navigate(Route.AddOffer.name)
                                        },
                                        modifier = Modifier
                                            .animateEnterExit(
                                                enter = scaleIn() + fadeIn(),
                                                exit = scaleOut() + fadeOut()
                                            )
                                    ) {
                                        Icon(Icons.Default.Phone, contentDescription = "Airtime")
                                    }
                                }
                            }

                            // AnimatedVisibility for SMS
                            AnimatedVisibility(visible = isFabExpanded) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        viewModel.setOfferTypeToAdd(OfferType.SMS)
                                        navController.navigate(Route.AddOffer.name)
                                    }
                                ) {
                                    Text(
                                        text = "SMS",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.animateEnterExit(
                                            enter = slideInHorizontally() + fadeIn(),
                                            exit = slideOutHorizontally() + fadeOut()
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    FloatingActionButton(
                                        onClick = {
                                            viewModel.setOfferTypeToAdd(OfferType.SMS)
                                            navController.navigate(Route.AddOffer.name)
                                        },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .animateEnterExit(
                                                enter = scaleIn() + fadeIn(),
                                                exit = scaleOut() + fadeOut()
                                            )
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.Message,
                                            contentDescription = "SMS"
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    FloatingActionButton(
                        onClick = { isFabExpanded = !isFabExpanded },
                        modifier = Modifier
                            .align(Alignment.End)
                            .zIndex(2f)
                    ) {
                        Icon(
                            modifier = Modifier
                                .rotate(rotationAngle),
                            imageVector = if (isFabExpanded) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = if (isFabExpanded) "Close" else "Add"
                        )
                    }
                }
            }
        }
    }
}